package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@Validated
@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;
    @Autowired
    private UserService userService;

    @PostMapping("/admin/create")
    public ResponseEntity<?> create(@Validated @RequestBody FacilityRequestDTO requestDTO,
                                    BindingResult bindingResult) throws Exception {
        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(facilityService.createFacility(requestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo cơ sở: " + e.getMessage());
        }
    }
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Validated @RequestBody FacilityUpdateDTO facilityUpdateDTO,
                                    BindingResult bindingResult) throws Exception {

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        try {
            facilityService.updateFacility(id, facilityUpdateDTO);
            return ResponseEntity.ok(facilityUpdateDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy cơ sở có id = " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi cập nhật cơ sở: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null ||
                (user.getRole() != 0)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hãy đăng nhập với tư cách chủ nhiệm câu lạc bộ");
        }

        try {
            facilityService.deleteFacility(id);
            return ResponseEntity.ok("Đã xóa cơ sở có ID = " + id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy cơ sở có id = " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi xóa cơ sở: " + e.getMessage());
        }
    }


    private ResponseEntity<?> checkBindingResult(BindingResult bindingResult) {
        // BindingResult store valid error, then log and return to front-end
        if (bindingResult.hasErrors()) {
            List<String> fieldOrder = List.of(
                    "name",
                    "address",
                    "phone",
                    "note");
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .sorted(Comparator.comparingInt(e -> fieldOrder.indexOf(e.getField())))
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();

            System.out.println(errors);
            return ResponseEntity.badRequest().body(errors.getFirst());
        }
        return null;
    }

    @GetMapping("/homepage")
    public ResponseEntity<?> getFacilitiesHomepage() {
        return ResponseEntity.ok(facilityService.getAllFacilitiesForHomepage());
    }

    @GetMapping("/admin/website-management")
    public ResponseEntity<?> getAllFacilitiesForWebsiteManagement(@AuthenticationPrincipal User user) {
        return  ResponseEntity.ok(facilityService.getAllFacilitiesForWebsiteManagement(user));
    }

    @GetMapping("/admin/management")
    public ResponseEntity<?> getAllFacilitiesForManagement(@AuthenticationPrincipal User user) {
        return  ResponseEntity.ok(facilityService.getAllFacilitiesForManagement(user));
    }
}
