package hr.gameshopweb.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Centralno mjesto za JWT autentikaciju preko httpOnly cookieja (web).
 * Token se NIKAD ne sprema u localStorage (XSS), nego u httpOnly + Secure +
 * SameSite=Lax cookie kojeg JavaScript ne moze procitati. Lax (a ne Strict) jer
 * cookie mora prezivjeti top-level redirect natrag s PayPala; Strict bi ga
 * odbio na povratku i korisnik bi ispao odjavljen.
 */
@Service
@RequiredArgsConstructor
public class CookieAuthService {

    public static final String ACCESS_COOKIE = "accessToken";
    public static final String REFRESH_COOKIE = "refreshToken";

    private final JwtService jwtService;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /** Nakon uspjesnog logina: izdaj access i refresh token kao cookieje. */
    public void issueCookies(HttpServletRequest req, HttpServletResponse res, String username) {
        issueAccessCookie(req, res, username);
        addCookie(req, res, REFRESH_COOKIE, jwtService.generateRefreshToken(username), refreshExpirationMs);
    }

    /** Izda (ili obnovi) samo access cookie - koristi auto-refresh u filteru. */
    public void issueAccessCookie(HttpServletRequest req, HttpServletResponse res, String username) {
        addCookie(req, res, ACCESS_COOKIE, jwtService.generateAccessToken(username), accessExpirationMs);
    }

    /** Procitaj refresh cookie i, ako je valjan, izdaj novi access cookie. */
    public boolean refreshAccessToken(HttpServletRequest req, HttpServletResponse res) {
        String refresh = readCookie(req, REFRESH_COOKIE);
        if (refresh == null || !jwtService.isValid(refresh)) {
            return false;
        }
        String username = jwtService.extractUsername(refresh);
        addCookie(req, res, ACCESS_COOKIE, jwtService.generateAccessToken(username), accessExpirationMs);
        return true;
    }

    /** Logout: obrisi oba cookieja (maxAge 0). */
    public void clearCookies(HttpServletRequest req, HttpServletResponse res) {
        addCookie(req, res, ACCESS_COOKIE, "", 0);
        addCookie(req, res, REFRESH_COOKIE, "", 0);
    }

    private void addCookie(HttpServletRequest req, HttpServletResponse res,
                           String name, String value, long maxAgeMs) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)            // JavaScript ne moze citati -> XSS-safe
                .secure(req.isSecure())    // salje se samo preko HTTPS-a (na localhostu ostaje http)
                .sameSite("Lax")           // salje se na top-level GET navigaciju (npr. PayPal
                                           // redirect natrag), ali ne na cross-site POST -> CSRF zastita
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String readCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) {
            return null;
        }
        for (Cookie c : req.getCookies()) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
