package hr.gameshopweb.controller.rest;

import hr.gameshopweb.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        body.get("username"), body.get("password"))
        );
        String username = auth.getName();
        return ResponseEntity.ok(Map.of(
                "accessToken", jwtService.generateAccessToken(username),
                "refreshToken", jwtService.generateRefreshToken(username)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (!jwtService.isValid(refreshToken))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        String username = jwtService.extractUsername(refreshToken);
        return ResponseEntity.ok(Map.of(
                "accessToken", jwtService.generateAccessToken(username)
        ));
    }
}