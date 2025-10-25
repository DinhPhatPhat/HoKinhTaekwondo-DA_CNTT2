package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.LoginRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(loginRequestDTO);
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@Validated @RequestBody UserCreateDTO userCreateDTO,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập.");
        }
        if (user.getRole() > 1 || user.getRole() >= userCreateDTO.getRole()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền tạo người dùng này.");
        }

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        if (userService.existsById(userCreateDTO.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("ID người dùng đã tồn tại");
        }

        if (user.getRole() == 1 && (userService.isManagerOfFacility(user.getId(), userCreateDTO.getFacilityId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không phải quản lý của cơ sở này.");
        }

        try {
            userService.create(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã tạo người dùng " + userCreateDTO.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }


    @PutMapping("update")
    public ResponseEntity<?> update(@Validated @RequestBody UserUpdateDTO userUpdateDTO,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");
        }
        //Coach or Instructor
        if (user.getRole() > 1 && !Objects.equals(user.getId(), userUpdateDTO.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Huấn luyện viên và hướng dẫn viên chỉ có thể cập nhật thông tin của mình.");
        }
        //Manager or Club Head
        else{
            if (user.getRole() >= userUpdateDTO.getRole() && !Objects.equals(user.getId(), userUpdateDTO.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chủ nhiệm và quản lý cơ sở có thể cập nhật thông tin bản thân hoặc người dưới quyền.");
            }
        }

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }


        if(user.getRole() == 1 && userService.isManagerOfFacility(user.getId(), userUpdateDTO.getFacilityId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không phải quản lý của cơ sở này.");
        }
        try {
            userService.update(userUpdateDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Cập nhật người dùng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }

    private ResponseEntity<?> checkBindingResult(BindingResult bindingResult) {
        // BindingResult store valid error, then log and return to front-end
        if (bindingResult.hasErrors()) {
            List<String> fieldOrder = List.of(
                    "id",
                    "name",
                    "phoneNumber",
                    "dateOfBirth",
                    "email",
                    "password",
                    "avatar",
                    "role");
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .sorted(Comparator.comparingInt(e -> fieldOrder.indexOf(e.getField())))
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();

            System.out.println(errors);
            return ResponseEntity.badRequest().body(errors.getFirst());
        }
        return null;
    }

}
