package com.mylstech.rentro.config;

import com.mylstech.rentro.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String API_V_1_PRODUCTS = "/api/v1/products";
    private static final String ALL = "/**";
    private static final String API_V_1_AUTH = "/api/v1/auth";
    private static final String PAYMENT = "/api/v1/payments";
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    @Value("${zrok.url}")
    public String zrokUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf ( AbstractHttpConfigurer::disable )
                .cors ( cors -> cors.configurationSource ( corsConfigurationSource ( ) ) )
                .authorizeHttpRequests ( auth -> auth
                        .requestMatchers ( HttpMethod.POST,
                                "/api/v1/job-applicants",
                                "/api/v1/files/upload-pdf",
                                "/api/v1/request-quotations",
                                "/api/v1/images/upload" ).permitAll ( )
                        .requestMatchers ( HttpMethod.POST,API_V_1_PRODUCTS ).hasRole ( "ADMIN" )
                        .requestMatchers ( HttpMethod.PUT,API_V_1_PRODUCTS ).hasRole ( "ADMIN" )
                        .requestMatchers ( HttpMethod.GET,
                                API_V_1_PRODUCTS + ALL,
                                "/api/v1/job-posts" + ALL,
                                "/api/v1/our-services" + ALL,
                                "/api/v1/clients",
                                "/api/v1/brands",
                                "/api/v1/banners",
                                "/api/v1/about-us" + ALL ,
                                "/uploads" + ALL,
                                API_V_1_AUTH + "/register",
                                API_V_1_AUTH + "/register-admin",
                                API_V_1_AUTH + "/initiate-auth",
                                API_V_1_AUTH + "/initiate-auth-phoneNo",
                                API_V_1_AUTH + "/complete-auth",
                                API_V_1_AUTH + "/admin-login",
                                API_V_1_AUTH + "/refresh-token",
                                // Swagger UI endpoints
                                "/swagger-ui" + ALL,
                                "/swagger-ui.html",
                                "/v3/api-docs" + ALL,
                                "/api-docs" + ALL,
                                "/swagger-resources" + ALL,
                                "/webjars" + ALL,
                                PAYMENT + "/create-payment-intent",
                                PAYMENT + "/confirm" + ALL
                        ).permitAll ( )
                        .anyRequest ( ).authenticated ( )
                )
                .sessionManagement ( session -> session
                        .sessionCreationPolicy ( SessionCreationPolicy.STATELESS )
                )

                .authenticationProvider ( authenticationProvider ( ) )
                .addFilterBefore ( jwtAuthFilter, UsernamePasswordAuthenticationFilter.class )
                .build ( );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider ( );
        authProvider.setUserDetailsService ( userDetailsService );
        authProvider.setPasswordEncoder ( passwordEncoder ( ) );
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager ( );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration ( );
        config.setAllowedOrigins ( List.of (
                "http://localhost:5173",
                "http://localhost:5174",
                zrokUrl,
                "https://testing.rentro.ae",
                "https://rentro.ae",
                "https://demo.rentro.ae",
                "https://panel.rentro.ae",
                "https://demo.panel.rentro.ae" ) ); // You can replace "*" with specific domains
        config.setAllowedMethods ( List.of ( "GET", "POST", "PUT", "DELETE", "OPTIONS" ) );
        config.setAllowedHeaders ( List.of ( "Authorization", "Content-Type", "skip_zrok_interstitial" ) );
        config.setAllowCredentials ( true ); // Set to false if you're not supporting cookies/auth headers cross-origin

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource ( );
        source.registerCorsConfiguration ( ALL, config );
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder ( );
    }
}