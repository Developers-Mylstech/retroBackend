package com.mylstech.rentro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer ( ) {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping ( "/**" )
//                        .allowedOrigins ( "http://testing.rentro.ae", "https://demo.rentro.ae", "http://localhost:5173", "https://yqndqaeqly2o.share.zrok.io" )
                        .allowedOrigins ( "*" )
                        .allowedMethods ( "*" )
                        .allowedHeaders ( "*" )
                        .allowCredentials ( true ); // Only if you’re using cookies/auth headers
            }
        };
    }


    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter ( );
        filter.setIncludeQueryString ( true );
        filter.setIncludePayload ( true );
        filter.setIncludeHeaders ( true ); // Be careful—can include sensitive info
        filter.setMaxPayloadLength ( 10000 );
        filter.setIncludeClientInfo ( true );
        return filter;
    }

}
