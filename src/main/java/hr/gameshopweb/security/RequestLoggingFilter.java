package hr.gameshopweb.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) req;
        String user = http.getUserPrincipal() != null
                ? http.getUserPrincipal().getName() : "anonymous";
        log.info("[{}] {} {} - user: {}",
                http.getRemoteAddr(), http.getMethod(),
                http.getRequestURI(), user);
        chain.doFilter(req, res);
    }



}