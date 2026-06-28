package hr.gameshopweb.config;

import hr.gameshopweb.security.CustomUserDetailsService;
import hr.gameshopweb.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    // --- REST API chain (stateless, JWT) ---
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // --- MVC chain (session, form login) ---
    @Bean
    @Order(2)
    public SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/shop/**", "/cart/**", "/auth/**",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/orders/**", "/checkout/**")
                        .hasAnyRole("KUPAC", ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/shop", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/shop")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .authenticationProvider(authenticationProvider());
        return http.build();
    }

    // Onemogucuje da Spring Boot automatski registrira JWT filter na sve rute
    // jer ga koristimo samo unutar REST API lanca
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(
            JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}