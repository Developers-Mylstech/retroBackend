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

    private static final String API_V_1_PRODUCTS = "api/v1/products";
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
                        .requestMatchers ( HttpMethod.GET,
                                API_V_1_PRODUCTS + "/**",
                                "/api/v1/job-posts/**",
                                "/api/v1/our-services/**",
                                "/api/v1/clients",
                                "/api/v1/brands",
                                "/api/v1/banners",
                                "/api/v1/about-us/**" ).permitAll ( )
                        .requestMatchers ( HttpMethod.POST,
                                "/api/v1/job-applicants",
                                "/api/v1/files/upload-pdf",
                                "/api/v1/request-quotations",
                                "/api/v1/image-entities/upload",
                                "/api/v1/images/upload"
                        ).permitAll ( )
                        .requestMatchers ( HttpMethod.POST,API_V_1_PRODUCTS,"/api/v1/about-us/**" ).hasRole ( "ADMIN" )
                        .requestMatchers ( HttpMethod.PUT,"api/v1/products","/api/v1/about-us/**" ).hasRole ( "ADMIN" )
                        .requestMatchers (
                                "/uploads/**",
                                "/api/v1/auth/**",
                                // Swagger UI endpoints
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/v1/payments/create-payment-intent",
                                "/api/v1/payments/confirm/**"
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
        source.registerCorsConfiguration ( "/**", config );
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder ( );
    }
}