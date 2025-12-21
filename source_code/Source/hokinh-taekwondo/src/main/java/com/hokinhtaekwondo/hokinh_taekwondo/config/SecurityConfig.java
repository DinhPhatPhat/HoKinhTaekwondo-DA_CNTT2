package com.hokinhtaekwondo.hokinh_taekwondo.config;

import com.hokinhtaekwondo.hokinh_taekwondo.security.JwtAuthenticationFilter;
import com.hokinhtaekwondo.hokinh_taekwondo.security.JwtAuthorizationFilter;
import com.hokinhtaekwondo.hokinh_taekwondo.security.JwtProvider;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("prod")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthorizationFilter jwtAuthorizationFilter
    ) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/*/homepage").permitAll()
                        .requestMatchers("/api/*/admin/**").hasAnyRole("CLUB_HEAD", "MANAGER")
                        .requestMatchers("/api/*/instructor/**").hasAnyRole("COACH", "INSTRUCTOR")
                        .requestMatchers("/api/*/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider,
            UserService userService
    ) {
        return new JwtAuthenticationFilter(authenticationManager, userService, jwtProvider);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            JwtProvider jwtProvider,
            UserService userService
    ) {
        return new JwtAuthorizationFilter(jwtProvider, userService);
    }

}
