package com.hokinhtaekwondo.hokinh_taekwondo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile("prod")
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // 👈 enable CORS
                .authorizeHttpRequests(auth -> auth
                        // 👇 allow preflight (OPTIONS) requests from browsers
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 👇 allow login/signup endpoints
                        .requestMatchers("/api/user/login", "/api/user").permitAll()
                        .requestMatchers("/api/facility/homepage", "/api/facility/all_facilities").permitAll()
                        .requestMatchers("/api/award/homepage").permitAll()
                        .requestMatchers("/api/article/homepage").permitAll()
                        // 👇 everything else must be authenticated
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

