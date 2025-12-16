package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.CheckinRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.CheckinResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.StudentAttendanceDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.StudentReviewDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.*;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.*;
import com.hokinhtaekwondo.hokinh_taekwondo.service.SessionUserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ValidateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/session-users")
@RequiredArgsConstructor
public class SessionUserController {

    private final UserService userService;
    private final SessionUserService sessionUserService;
    private final ValidateService validateService;
    // ====================== CHECKIN ======================
    @PutMapping("/instructor/checkin")
    public ResponseEntity<?> checkin(@RequestBody CheckinRequestDTO dto,
                                     @AuthenticationPrincipal User user) throws Exception {
        try {
            CheckinResponseDTO result = sessionUserService.checkin(user.getId(), dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống khi check-in: " + e.getMessage());
        }
    }

    @PostMapping("/mark-review")
    public ResponseEntity<?> markReview(
            @RequestBody StudentReviewDTO dto,
            HttpSession session,
            BindingResult bindingResult,
            @CookieValue(value = "token", required = false) String token) {

        User currentUser = userService.getCurrentUser(session, token);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Hãy đăng nhập trước khi thực hiện.");
        }

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            // Gọi service đánh giá
            sessionUserService.markReview(currentUser.getId(), dto);

            return ResponseEntity.ok("Đánh giá thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping("/instructor/student-data")
    public ResponseEntity<?> getAllStudentsInInstructorSession(@AuthenticationPrincipal User user,
                                                               @RequestParam Integer sessionId) {
        try {
            return ResponseEntity.ok(sessionUserService.getStudentAttendances(sessionId, user.getId()));
        }
        catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
