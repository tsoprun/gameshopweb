package hr.gameshopweb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Autentikacija weba preko JWT cookieja (stateless).
 * - ako je access cookie valjan -> autentificiraj
 * - ako access fali/istekao a refresh je valjan -> izda novi access (silent refresh) i autentificiraj
 * - ako oba fale/nevaljana -> nema autentikacije -> Spring preusmjeri na login
 */
@Component
@RequiredArgsConstructor
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final CookieAuthService cookieAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = resolveUsername(req, res);
            if (username != null) {
                UserDetails ud = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(
                        ud, null, ud.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res);
    }

    private String resolveUsername(HttpServletRequest req, HttpServletResponse res) {
        String access = readCookie(req, CookieAuthService.ACCESS_COOKIE);
        if (access != null && jwtService.isValid(access)) {
            return jwtService.extractUsername(access);
        }
        // access fali/istekao -> probaj refresh
        String refresh = readCookie(req, CookieAuthService.REFRESH_COOKIE);
        if (refresh != null && jwtService.isValid(refresh)) {
            String username = jwtService.extractUsername(refresh);
            cookieAuthService.issueAccessCookie(req, res, username);
            return username;
        }
        return null;
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
