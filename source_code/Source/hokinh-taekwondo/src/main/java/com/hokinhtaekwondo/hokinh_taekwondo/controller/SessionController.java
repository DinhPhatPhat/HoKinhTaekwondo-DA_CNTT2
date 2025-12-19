package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor.SessionStatisticsForInstructor;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.FullSessionUserDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.SessionService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ValidateService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ValidateService validateService;

    // ================= BULK CREATE MULTI-DAY SESSION AND USER ==================
    @PostMapping("/admin/bulk-create-multi-day")
    public ResponseEntity<?> bulkCreateSessionsAndUsers(
            @RequestParam Integer facilityClassId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Valid @RequestBody List<SessionAndSessionUserBulkCreateDTO> dtoList,
            BindingResult bindingResult,
            HttpSession session,
            @CookieValue(value = "token", required = false) String token) throws Exception {

//        User currentUser = userService.getCurrentUser(session, token);
//        if (currentUser == null)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Hãy đăng nhập trước khi thực hiện.");
//
//        if (currentUser.getRole() > 1)
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Bạn không có quyền thêm buổi học.");
        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
        if (daysBetween > 1000) {
            throw new IllegalArgumentException("Khoảng thời gian tạo session không được vượt quá 90 ngày.");
        }
        try {
            int createdCount = sessionService.bulkCreateSessionsAndUsers(facilityClassId, startDate, endDate, dtoList);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã thêm " + createdCount + " buổi học thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi thêm buổi học: " + e.getMessage());
        }
    }

    // ======== BULK UPDATE =========
    @PutMapping("/admin/bulk-update")
    public ResponseEntity<?> bulkUpdateSessions(
            @Valid @RequestBody SessionBulkUpdateDTO sessionBulkUpdateDTO,
            BindingResult bindingResult,
            HttpSession session,
            @CookieValue(value = "token", required = false) String token) throws Exception {

        User currentUser = userService.getCurrentUser(session, token);
        if (currentUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hãy đăng nhập trước khi thực hiện.");

        if (currentUser.getRole() > 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền chỉnh sửa buổi học.");

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            sessionService.bulkUpdateSessions(sessionBulkUpdateDTO);
            return ResponseEntity.ok("Đã cập nhật " + sessionBulkUpdateDTO.getSessions().size() + " buổi học của lớp " + sessionBulkUpdateDTO.getFacilityClassId() + " thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi cập nhật buổi học: " + e.getMessage());
        }
    }

    @GetMapping("/admin/{facilityClassId}")
    public ResponseEntity<?> getSessionsByFacilityClassIdAndDateRange(
            @PathVariable Integer facilityClassId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            HttpSession session,
            @CookieValue(value = "token", required = false) String token) throws Exception {

        User currentUser = userService.getCurrentUser(session, token);
        if (currentUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hãy đăng nhập trước khi thực hiện.");
        if (startDate == null || endDate == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng cung cấp đầy đủ thời gian bắt đầu và kết thúc.");

        if (endDate.isBefore(startDate))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.");
        try {
            var sessions = sessionService.getSessionsByFacilityClassAndDateRange(facilityClassId, startDate, endDate);
            return ResponseEntity.ok(sessions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách buổi học: " + e.getMessage());
        }
    }

    // 1. GET sessions in range
    @GetMapping("/admin/session-user-data")
    public ResponseEntity<?> getSessions(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Integer classId
    ) {
        System.out.println(startDate + " " + endDate + " " + classId);
        try {
            return ResponseEntity.ok(sessionService.getSessions(
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    classId));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // 2. GET students in session
    @GetMapping("/admin/{sessionId}/student-data")
    public ResponseEntity<?> getStudents(@PathVariable Integer sessionId) {
        try {
            return ResponseEntity.ok(sessionService.getStudentsOfSession(sessionId));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // 3. UPDATE session + session users
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> update(
            @RequestBody SessionAndUserUpdateDTO req,
            @PathVariable Integer id
    ) {
        try {
            return ResponseEntity.ok(sessionService.updateSession(req, id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> create(@RequestBody @Valid SessionAndUserCreateDTO req) {
        try {
            return ResponseEntity.ok(sessionService.createSession(req));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            System.out.println("session " + id);
            return ResponseEntity.ok(sessionService.deleteSession(id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/admin/statistics-instructor-session")
    public ResponseEntity<?> getStatisticsInstructorSession(@RequestParam Integer facilityId,
                                                            @RequestParam String startDate,
                                                            @RequestParam String endDate) {
        try {
            return ResponseEntity.ok(sessionService.getInstructorSessionAttendancesStatistics(facilityId,
                    LocalDate.parse(startDate), LocalDate.parse(endDate)));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/instructor/session-user-data")
    public ResponseEntity<?> getInstructorSessions(@AuthenticationPrincipal User currentUser,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) {
        try {
            return ResponseEntity.ok(sessionService.getInstructorSessions(LocalDate.parse(startDate), LocalDate.parse(endDate), currentUser.getId()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/instructor/report-session")
    public ResponseEntity<?> reportSession(@AuthenticationPrincipal User instructor,
                                           @RequestBody InstructorSessionUpdateDTO updatedSession) {
        try {
            sessionService.reportSession(updatedSession, instructor.getId());
            return ResponseEntity.ok("Báo cáo buổi học thành công");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/instructor/session-statistics")
    public ResponseEntity<?> getStatisticsForInstructor(@AuthenticationPrincipal User instructor,
                                                        @RequestParam String startDate,
                                                        @RequestParam String endDate) {
        try {
            SessionStatisticsForInstructor result = sessionService.getSessionStatisticsForInstructor(
                    instructor.getId(),
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate)
            );
            return  ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}