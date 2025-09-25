package com.ayd.sie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication. Format: Bearer {token}")));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("01-admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi coordinatorApi() {
        return GroupedOpenApi.builder()
                .group("02-coordinator")
                .pathsToMatch("/coordinator/**")
                .build();
    }

    @Bean
    public GroupedOpenApi courierApi() {
        return GroupedOpenApi.builder()
                .group("03-courier")
                .pathsToMatch("/courier/**")
                .build();
    }

    @Bean
    public GroupedOpenApi businessApi() {
        return GroupedOpenApi.builder()
                .group("04-business")
                .pathsToMatch("/business/**")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("05-public")
                .pathsToMatch("/public/**", "/auth/**", "/tracking/public/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("00-all-endpoints")
                .pathsToMatch("/**")
                .build();
    }

    private Info getApiInfo() {
        return new Info()
                .title("Sistema Integral de Entregas (SIE) API")
                .description("RESTful API for comprehensive delivery management system including user management, tracking guides, courier assignments, business affiliations, and loyalty programs.\n\n"
                        +
                        "**Base URL Structure:**\n" +
                        "- Admin endpoints: `/api/v1/admin/**`\n" +
                        "- Coordinator endpoints: `/api/v1/coordinator/**`\n" +
                        "- Courier endpoints: `/api/v1/courier/**`\n" +
                        "- Business endpoints: `/api/v1/business/**`\n" +
                        "- Public endpoints: `/api/v1/public/**`\n" +
                        "- Authentication endpoints: `/api/v1/auth/**`")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Diego Maldonado")
                        .email("dmaldonado1920@gmail.com")
                        .url("https://github.com/djmaldonado19"))
                .license(new License()
                        .name("Private License")
                        .url("https://www.example.com/license"));
    }

    private List<Server> getServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080/api/v1")
                        .description("Development Server (Local)"),
                new Server()
                        .url("http://20.55.81.100:8080/api/v1")
                        .description("Production Server (Azure HTTP)"),
                new Server()
                        .url("https://20.55.81.100:8080/api/v1")
                        .description("Production Server (Azure HTTPS)"));
    }
}