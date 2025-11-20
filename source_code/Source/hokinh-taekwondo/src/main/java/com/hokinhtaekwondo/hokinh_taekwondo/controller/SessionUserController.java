package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.CheckinRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.*;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.*;
import com.hokinhtaekwondo.hokinh_taekwondo.service.SessionUserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/session-users")
@RequiredArgsConstructor
public class SessionUserController {

    private final UserService userService;
    private final SessionUserService sessionUserService;

    // ====================== CHECKIN ======================
    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(@RequestBody CheckinRequestDTO dto,
                                     BindingResult bindingResult,
                                     HttpSession session,
                                     @CookieValue(value = "token", required = false) String token) throws Exception {

        User currentUser = userService.getCurrentUser(session, token);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Hãy đăng nhập trước khi thực hiện.");
        }

        try {
            String result = sessionUserService.checkin(currentUser.getId(), dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống khi check-in: " + e.getMessage());
        }
    }

}
