package com.hokinhtaekwondo.hokinh_taekwondo.security;

import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${app.secretkey}")
    private String SECRET_KEY;
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;
    private final UserRepository userRepository;

    // Generate a JWT token using userId as the subject
    public String generateToken(String userId) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Integer loginPin = redisTemplate.opsForValue().get("loginPin:" + userId);
        if(loginPin == null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với id: " + userId));
            loginPin = user.getLoginPin();
            redisTemplate.opsForValue().set("loginPin:" + userId, loginPin);
        }
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) // 20 days
                .claim("loginPin", loginPin)
                .signWith(key)
                .compact();
    }

    // Extract userId from the JWT token
    public String extractUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Extract userId from the JWT token
    public Integer extractLoginPin(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("loginPin", Integer.class);
    }

    // Validate if the token is valid
    public boolean isTokenValid(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
