package com.hokinhtaekwondo.hokinh_taekwondo.security;

import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String userId = jwtProvider.extractUserId(token);
                User userDetails = userService.loadUserByUsername(userId);
                System.out.println("Authentication Filter Called");
                System.out.println(userDetails.getAuthorities());

                Integer jwtLoginPin = jwtProvider.extractLoginPin(token);
                Integer currentLoginPin = userDetails.getLoginPin();

                if(Objects.equals(jwtLoginPin, currentLoginPin)) {
                    if(!userDetails.isEnabled()) {
                        sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Tài khoản của bạn đã bị khóa. Hãy liên hệ quản lý để mở khóa");
                        return;
                    }

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Phiên đăng nhập đã hết. Vui lòng đăng nhập lại");
                    return;
                }
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token đã hết hạn. Vui lòng đăng nhập lại");
                return;
            } catch (JwtException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            } catch (UsernameNotFoundException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Người dùng không tồn tại");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}