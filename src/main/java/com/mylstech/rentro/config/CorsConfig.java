package com.mylstech.rentro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://svgd9vgy3l8x.share.zrok.io", "http://localhost:8080","http://localhost:5173","http://javatest.mylstech.com:8080") // <-- replace with your real zrok URL
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true); // Only if you’re using cookies/auth headers
            }
        };
    }
}
