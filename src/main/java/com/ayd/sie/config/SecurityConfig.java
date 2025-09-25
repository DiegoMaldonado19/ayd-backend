package com.ayd.sie.config;

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

    // JWT Authentication Entry Point will be implemented later
    // private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // JWT Authentication Filter will be implemented later
    // private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                        // Swagger/OpenAPI endpoints - DEBE IR PRIMERO
                        .requestMatchers("/api/v1/swagger-ui/**").permitAll()
                        .requestMatchers("/api/v1/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Public endpoints - no authentication required
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/tracking/public/**").permitAll()

                        // Test endpoints - for development/verification (specify each one explicitly)
                        .requestMatchers("/api/v1/admin/test/**").permitAll()
                        .requestMatchers("/api/v1/coordinator/test/**").permitAll()
                        .requestMatchers("/api/v1/courier/test/**").permitAll()
                        .requestMatchers("/api/v1/business/test/**").permitAll()

                        // Actuator endpoints
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Static resources
                        .requestMatchers("/uploads/**").permitAll()

                        // Admin endpoints - only for administrators (DESPUES de los test endpoints)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Coordinator endpoints - for coordinators and admins
                        .requestMatchers("/api/v1/coordinator/**").hasAnyRole("ADMIN", "COORDINATOR")

                        // Courier endpoints - for couriers, coordinators and admins
                        .requestMatchers("/api/v1/courier/**").hasAnyRole("ADMIN", "COORDINATOR", "COURIER")

                        // Business endpoints - for businesses, coordinators and admins
                        .requestMatchers("/api/v1/business/**").hasAnyRole("ADMIN", "COORDINATOR", "BUSINESS")

                        // All other requests require authentication
                        .anyRequest().authenticated());

        // Exception handling will be implemented later
        // .exceptionHandling(exception -> exception
        // .authenticationEntryPoint(jwtAuthenticationEntryPoint))

        // JWT filter will be added later
        // .addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);

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