package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.FacilityClassService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/facility-class")
public class FacilityClassController {

    @Autowired
    private FacilityClassService facilityClassService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody FacilityClassCreateDTO dto,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hãy đăng nhập trước khi thực hiện.");
        }
        if (user.getRole() > 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền tạo lớp.");
        }

        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), dto.getFacilityId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không quản lý cơ sở này.");
        }

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            facilityClassService.createFacilityClass(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã tạo lớp " + dto.getName() + " tại cơ sở " + dto.getFacilityId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo lớp: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody FacilityClassUpdateDTO dto,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hãy đăng nhập.");
        }
        if (user.getRole() > 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền cập nhật lớp.");
        }

        FacilityClass existing = facilityClassService.getById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp có ID = " + id);
        }

        // Kiểm tra quyền quản lý của role 1
        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), existing.getFacility().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không quản lý cơ sở chứa lớp này.");
        }

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            facilityClassService.updateFacilityClass(id, dto);
            return ResponseEntity.ok("Đã cập nhật lớp " + dto.getName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi cập nhật lớp: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null || user.getRole() > 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền xóa lớp.");
        }

        FacilityClass facilityClass = facilityClassService.getById(id);
        if (facilityClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy lớp có id = " + id);
        }

        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), facilityClass.getFacility().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không quản lý cơ sở chứa lớp này.");
        }

        try {
            facilityClassService.deleteFacilityClass(id);
            return ResponseEntity.ok("Đã xóa lớp có ID = " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi xóa lớp: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getFacilityClasses() {
        return ResponseEntity.status(HttpStatus.OK).body(facilityClassService.getAllFacilityClasses());
    }



    private ResponseEntity<?> checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Object target = bindingResult.getTarget();
            List<String> fieldOrder = new ArrayList<>();
            if (target != null) {
                for (Field field : target.getClass().getDeclaredFields()) {
                    fieldOrder.add(field.getName());
                }
            }
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .sorted(Comparator.comparingInt(e -> fieldOrder.indexOf(e.getField())))
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.getFirst());
        }
        return null;
    }
}
