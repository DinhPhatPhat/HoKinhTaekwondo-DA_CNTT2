package com.hokinhtaekwondo.hokinh_taekwondo.security;

import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, UsernameNotFoundException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            // Remove bearer_
            String token = header.substring(7);

            String userId = jwtProvider.extractUserId(token);
            User userDetails = userService.loadUserByUsername(userId);
            System.out.println("Authentication Filter Called");
            System.out.println(userDetails.getAuthorities());

            Integer jwtLoginPin = jwtProvider.extractLoginPin(token);
            Integer currentLoginPin = userDetails.getLoginPin();

            if(Objects.equals(jwtLoginPin, currentLoginPin)) {
                // Not authenticate user if they were banned
                if(!userDetails.isEnabled()) {
                    throw new RuntimeException("Tài khoản của bạn đã bị khóa. Hãy liên hệ quản lý để mở khóa");
                }
                // Prepare the authentication info to send to controller
                UsernamePasswordAuthenticationToken auth =  new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // Send authentication to controller
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else {
                throw new RuntimeException("Phiên đăng nhập đã hết. Vui lòng đăng nhập lại");
            }
        }
        filterChain.doFilter(request, response);
    }
}
