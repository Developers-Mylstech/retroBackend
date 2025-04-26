package com.mylstech.rentro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info ()
                        .title("My API Documentation")
                        .version("v1")
                        .description("API documentation for My Spring Boot application"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://2b5sfnclbdz1.share.zrok.io") )
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8080/") )
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://demo.rentro.ae:8081") )
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://proud-expression-production-6ebc.up.railway.app") )
                ;
    }
}
