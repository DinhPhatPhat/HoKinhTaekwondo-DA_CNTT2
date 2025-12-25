package com.hokinhtaekwondo.hokinh_taekwondo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.LoginRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.LoginResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserLoginResponse;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService userDetailsService;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!request.getServletPath().equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("Authentication Filter Called");
        LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getId(),
                        loginRequestDTO.getPassword()
                );
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtProvider.generateToken(authentication.getName());
            // Get user info
            User user = userDetailsService.getById(userDetails.getUsername());
            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setToken(token);
            loginResponse.setUserInfo(new UserLoginResponse(user.getName(), user.getRole()));
            System.out.println(loginResponse.getUserInfo().getName());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            new ObjectMapper().writeValue(response.getWriter(), loginResponse);
        }
        catch (DisabledException e) {
            // Handle disabled account specifically
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Tài khoản đã bị vô hiệu hóa\"}");
        }
        catch (BadCredentialsException e) {
            // Handle wrong username/password
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Tên đăng nhập hoặc mật khẩu không đúng\"}");
        }
        catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
