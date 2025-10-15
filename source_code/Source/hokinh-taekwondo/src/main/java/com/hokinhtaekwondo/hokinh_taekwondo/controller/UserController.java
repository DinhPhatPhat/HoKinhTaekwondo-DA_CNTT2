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
                !(user.getRole() == User.Role.club_head || user.getRole() == User.Role.manager)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hãy đăng nhập với tư cách chủ tịch câu lạc bộ hoặc quản lý");
        }

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        if (userService.existsById(requestDTO.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("ID đã tồn tại");
        }

        // Kiểm tra quyền tạo theo vai trò
        User.Role creatorRole = user.getRole();
        User.Role newUserRole = User.Role.valueOf(requestDTO.getRole());

        if (creatorRole == User.Role.club_head) {
            if (!(newUserRole == User.Role.manager ||
                    newUserRole == User.Role.coach ||
                    newUserRole == User.Role.student)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Chủ tịch chỉ được tạo quản lý, huấn luyện viên hoặc học viên");
            }
        } else if (creatorRole == User.Role.manager) {
            if (!(newUserRole == User.Role.coach ||
                    newUserRole == User.Role.student)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Quản lý chỉ được tạo huấn luyện viên hoặc học viên");
            }
        }

        try {
            userService.create(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã tạo người dùng " + requestDTO.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }


    @PutMapping("update")
    public ResponseEntity<?> update(@Validated @RequestBody UserRequestDTO requestDTO,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @RequestParam(required = false) MultipartFile image,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null ||
                !(user.getRole() == User.Role.club_head || user.getRole() == User.Role.manager)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hãy đăng nhập với tư cách chủ tịch câu lạc bộ hoặc quản lý");
        }

        ResponseEntity<?> errorResponse = checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        if (image != null && image.getSize() > 3 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File ảnh cần bé hơn 3MB");
        }

        if (!userService.existsById(requestDTO.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại");
        }

        // --- Kiểm tra quyền cập nhật ---
        User.Role creatorRole = user.getRole();
        User.Role targetRole = User.Role.valueOf(requestDTO.getRole());

        if (creatorRole == User.Role.club_head) {
            if (!(targetRole == User.Role.manager ||
                    targetRole == User.Role.coach ||
                    targetRole == User.Role.student)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Chủ tịch chỉ được cập nhật quản lý, huấn luyện viên hoặc học viên");
            }
        } else if (creatorRole == User.Role.manager) {
            if (!(targetRole == User.Role.coach ||
                    targetRole == User.Role.student)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Quản lý chỉ được cập nhật huấn luyện viên hoặc học viên");
            }
        }

        try {
            userService.update(requestDTO, image);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Cập nhật người dùng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }

    @PostMapping("active/{id}")
    public ResponseEntity<?> activeUser(@PathVariable String id,
                                        HttpSession session,
                                        @CookieValue(value = "token", required = false) String token) throws Exception {
        User currentUser = userService.getCurrentUser(session, token);
        if (currentUser == null ||
                !(currentUser.getRole() == User.Role.club_head || currentUser.getRole() == User.Role.manager)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Chỉ chủ tịch câu lạc bộ hoặc quản lý mới có quyền kích hoạt tài khoản");
        }

        boolean result = userService.active(id);
        if (result) {
            return ResponseEntity.ok("Đã kích hoạt người dùng có ID: " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy người dùng có ID: " + id);
        }
    }

    @PostMapping("deactivate/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable String id,
                                            HttpSession session,
                                            @CookieValue(value = "token", required = false) String token) throws Exception {
        User currentUser = userService.getCurrentUser(session, token);
        if (currentUser == null ||
                !(currentUser.getRole() == User.Role.club_head || currentUser.getRole() == User.Role.manager)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Chỉ chủ tịch câu lạc bộ hoặc quản lý mới có quyền vô hiệu hóa tài khoản");
        }

        boolean result = userService.deactivate(id);
        if (result) {
            return ResponseEntity.ok("Đã vô hiệu hóa người dùng có ID: " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy người dùng có ID: " + id);
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
