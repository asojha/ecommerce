package com.ecommerce.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public: mobile auth
                .requestMatchers(
                    "/api/mobile/auth/register",
                    "/api/mobile/auth/login"
                ).permitAll()
                // Public: store webhooks (secured by payload signature / OIDC JWT)
                .requestMatchers("/api/webhooks/apple", "/api/webhooks/google").permitAll()
                // Public: existing web admin UI and its APIs (no mobile auth needed)
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/css/**",
                    "/js/**",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/api/products/**",
                    "/api/recommendations/**",
                    "/api/admin/store-mappings/**"
                ).permitAll()
                // Everything else requires JWT
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(fo -> fo.disable())) // H2 console
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
