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

                // Permitir orígenes específicos - desarrollo y producción
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                // Desarrollo local
                                "*"));

                // Métodos HTTP permitidos
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                // Permitir todos los headers
                configuration.setAllowedHeaders(Arrays.asList("*"));

                // Exponer headers necesarios
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "X-Total-Count"));

                // Permitir credenciales
                configuration.setAllowCredentials(true);

                // Cache de preflight por 1 hora
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}