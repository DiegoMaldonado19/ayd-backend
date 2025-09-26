package com.ayd.sie.config;

import com.ayd.sie.shared.infrastructure.security.JwtAuthenticationEntryPoint;
import com.ayd.sie.shared.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Configure session management - stateless for JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Public authentication endpoints - DEBE IR PRIMERO Y SER MUY ESPECÍFICO
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/verify-2fa").permitAll()
                        .requestMatchers("/auth/refresh-token").permitAll()
                        .requestMatchers("/auth/forgot-password").permitAll()
                        .requestMatchers("/auth/reset-password").permitAll()
                        .requestMatchers("/auth/validate-reset-token").permitAll()
                        .requestMatchers("/auth/resend-2fa-code").permitAll()
                        .requestMatchers("/auth/validate-token").permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/tracking/public/**").permitAll()

                        // Test endpoints - for development/verification
                        .requestMatchers("/admin/test/**").permitAll()
                        .requestMatchers("/coordinator/test/**").permitAll()
                        .requestMatchers("/courier/test/**").permitAll()
                        .requestMatchers("/business/test/**").permitAll()

                        // Actuator endpoints
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMINISTRADOR")

                        // Static resources
                        .requestMatchers("/uploads/**").permitAll()

                        // Protected authentication endpoints (require authentication)
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/auth/change-password").authenticated()
                        .requestMatchers("/auth/enable-2fa").authenticated()
                        .requestMatchers("/auth/disable-2fa").authenticated()
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/auth/sessions").authenticated()
                        .requestMatchers("/auth/revoke-token").authenticated()

                        // Admin endpoints - only for administrators
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")

                        // Coordinator endpoints - for coordinators and admins
                        .requestMatchers("/coordinator/**").hasAnyRole("ADMINISTRADOR", "COORDINADOR")

                        // Courier endpoints - for couriers, coordinators and admins
                        .requestMatchers("/courier/**").hasAnyRole("ADMINISTRADOR", "COORDINADOR", "REPARTIDOR")

                        // Business endpoints - for businesses, coordinators and admins
                        .requestMatchers("/business/**").hasAnyRole("ADMINISTRADOR", "COORDINADOR", "COMERCIO")

                        // All other requests require authentication
                        .anyRequest().authenticated())

                // Exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}