package com.byteandblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults -> {})
            .authorizeHttpRequests(auth -> auth
            	// Allow public access to static resources and root URL
                .requestMatchers(new AntPathRequestMatcher("/", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/index.html", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/static/**", "GET")).permitAll()
                // Allow public endpoints for GET requests
                .requestMatchers(new AntPathRequestMatcher("/Uploads/**", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/uploads/**", "GET")).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog", "/api/blog/**", "/api/portfolio","/api/comments/**").permitAll()
                // Allow public endpoints
                .requestMatchers("/api/auth/**", "/api/contact").permitAll()
                // Restrict blog and portfolio creation to ROLE_ADMIN
                .requestMatchers(HttpMethod.POST, "/api/blog").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/portfolio").hasRole("ADMIN")
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .securityMatcher(request -> {
                String path = request.getServletPath();
                return !(path.startsWith("/Uploads/") || path.startsWith("/uploads/"));
            });
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}