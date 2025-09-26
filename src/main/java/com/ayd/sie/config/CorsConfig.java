package com.ayd.sie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allow specific origins for development and production
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                // Local development - Postman testing
                                "http://localhost:*",
                                "http://127.0.0.1:*",

                                // Angular development server (puerto 4200 específico)
                                "http://localhost:4200",
                                "http://127.0.0.1:4200",

                                // Azure production server HTTP/HTTPS
                                "http://20.55.81.100:*",
                                "https://20.55.81.100:*",

                                // Docker internal network communication
                                "http://sie_backend:*",
                                "http://app:*"));

                // Allow all standard HTTP methods
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                // Allow all headers typically used by Angular applications
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "X-Requested-With",
                                "Accept",
                                "Origin",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers",
                                "X-CSRF-TOKEN"));

                // Expose headers that might be needed by the frontend
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Disposition",
                                "X-Total-Count"));

                // Allow credentials (cookies, authorization headers, TLS client certificates)
                configuration.setAllowCredentials(true);

                // Cache preflight response for 1 hour
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/v1/**", configuration);

                return source;
        }
}