package hr.gameshopweb.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Pokrece se nakon uspjesne form-login prijave: uz session (JSESSIONID) izda
 * i access + refresh JWT u httpOnly cookieje, pa preusmjeri na /shop.
 */
@Component
@RequiredArgsConstructor
public class JwtCookieSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CookieAuthService cookieAuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        cookieAuthService.issueCookies(request, response, authentication.getName());
        getRedirectStrategy().sendRedirect(request, response, "/shop");
    }
}
