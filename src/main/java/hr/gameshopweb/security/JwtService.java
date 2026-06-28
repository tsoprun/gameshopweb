package hr.gameshopweb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
// jjwt 0.11.x prima samo java.util.Date; interno racunamo preko java.time.Instant
@SuppressWarnings("java:S2143")
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String username) {
        return build(username, accessExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return build(username, refreshExpirationMs);
    }

    private String build(String username, long expMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expMs)))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public boolean isValid(String token) {
        try { parse(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody();
    }
}