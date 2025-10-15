package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.LoginRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.FacilityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FacilityService facilityService;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(loginRequestDTO);
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@Validated @RequestBody UserRequestDTO requestDTO,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null ||
                ! user.getRole().equals(User.Role.club_head)
                        && !user.getRole().equals(User.Role.manager)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hãy đăng nhập với tư cách trưởng câu lạc bộ hoặc quản lý");
        }
        ResponseEntity<?> errorResponse = checkBindingResult(requestDTO, bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        if (userService.existsById(requestDTO.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("ID đã tồn tại");
        }

        try {
            userService.create(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Đã tạo người dùng" + requestDTO.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@Validated @RequestBody UserRequestDTO requestDTO,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @RequestParam(required = false) MultipartFile image,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null || !user.getRole().equals(User.Role.club_head) && !user.getRole().equals(User.Role.manager)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phải là chủ câu lạc bộ hoặc quản lý mới có thể cập nhật người dùng");
        }
        ResponseEntity<?> errorResponse = checkBindingResult(requestDTO, bindingResult);

        if (image != null && image .getSize() > 3 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File ảnh cần bé hơn 3MB");
        }

        if (errorResponse != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kiểm tra thông tin đăng nhập");
        }
        if (userService.existsById(requestDTO.getId())) {
            try {
                userService.update(requestDTO, image);
                return ResponseEntity.status(HttpStatus.OK).body("Cập nhật người dùng thành công");
            }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi hệ thôống, thứ lại sau");
            }

        }
        return null;
    }

    private ResponseEntity<?> checkBindingResult(UserRequestDTO user ,BindingResult bindingResult) {
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
