package com.example.companyanalyzer.config;

import com.example.companyanalyzer.security.JwtAuthenticationFilter;
import com.example.companyanalyzer.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure CSRF, as we are using stateless session management for JWT
        // For APIs, CSRF is typically disabled or handled differently if session cookies are not used for auth.
        // If your Angular app and Spring Boot backend are on different domains, ensure CORS is configured.
        // Using XorCsrfTokenRequestAttributeHandler for compatibility with SPA if CSRF is needed.
        // However, for a typical JWT setup with Authorization header, CSRF protection might be disabled.
        // For simplicity in a pure JWT API, we often disable CSRF.
        http.csrf(csrf -> csrf.disable()); // Disabling CSRF for stateless JWT authentication

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll() // Allow auth endpoints
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console (for development)
                .anyRequest().authenticated() // Secure all other endpoints
        );

        // For H2 console to work with Spring Security, headers need to be configured
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));


        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
