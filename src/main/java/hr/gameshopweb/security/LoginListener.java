package hr.gameshopweb.security;

import hr.gameshopweb.repository.UserRepository;
import hr.gameshopweb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginListener
        implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String ip = extractIp();
        userRepository.findByUsername(username)
                .ifPresent(user -> userService.logLogin(user, ip));
    }

    private String extractIp() {
        try {
            var attrs = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            HttpServletRequest req = attrs.getRequest();
            String forwarded = req.getHeader("X-Forwarded-For");
            return (forwarded != null && !forwarded.isEmpty())
                    ? forwarded.split(",")[0].trim()
                    : req.getRemoteAddr();
        } catch (IllegalStateException e) {
            log.warn("Could not resolve client IP for login log", e);
            return "unknown";
        }
    }
}