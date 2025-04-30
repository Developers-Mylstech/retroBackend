package com.mylstech.rentro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Rentro API Documentation")
                        .version("v1")
                        .description("API documentation for Rentro application")
                        .contact(new Contact()
                                .name("Rentro Team")
                                .email("support@rentro.ae")
                                .url("https://rentro.ae"))
                        .license(new License().name("Rentro License")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://guqsff2ya7r9.share.zrok.io"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8080/"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://demo.rentro.ae:8081"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://proud-expression-production-6ebc.up.railway.app"))
                // Security scheme definition
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token with Bearer prefix, e.g. 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'")
                        ))
                // Apply security globally to all operations
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}