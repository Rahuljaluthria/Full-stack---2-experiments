
package com.example.studentapi.config;

import com.example.studentapi.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // CSRF is disabled for this stateless bearer-token API.
            .csrf(csrf -> csrf.disable())

            // No HTTP session: requests authenticate using JWTs.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // Authentication endpoints are publicly accessible.
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/refresh"
                ).permitAll()

                // Allow browser preflight requests.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // H2 console access for local development.
                .requestMatchers("/h2-console/**").permitAll()

                // Both USER and ADMIN can read student records.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/students",
                    "/api/students/**"
                ).hasAnyRole("USER", "ADMIN")

                // Only ADMIN can modify student records.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/students",
                    "/api/students/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/students",
                    "/api/students/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/students",
                    "/api/students/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/students",
                    "/api/students/**"
                ).hasRole("ADMIN")

                // All other endpoints require authentication.
                .anyRequest().authenticated()
            )

            // Validate bearer tokens before Spring's username/password filter.
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
