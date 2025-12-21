package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports.UserImportResult;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.JwtService;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.exception.DuplicateUsersException;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.exception.export.UserImportErrorFileGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ValidateService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO loginRequestDTO, HttpSession session,
                                   BindingResult bindingResult,
                                   HttpServletResponse response) {

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        User user = userService.getById(loginRequestDTO.getId());
        if (user != null) {
            if (user.getPassword().equals(loginRequestDTO.getPassword())) {
                session.setAttribute("user", user);
                // Create token JWT
                String token = jwtService.generateToken(loginRequestDTO.getId());
                // Save token to cookie
                Cookie tokenCookie = new Cookie("token", token);
                tokenCookie.setHttpOnly(true);
                tokenCookie.setMaxAge(30 * 24 * 60 * 60); //30 days
                tokenCookie.setSecure(false); // ⚠️ Đặt lại thành true khi deploy HTTPS
                tokenCookie.setPath("/");  // Apply for app
                response.addCookie(tokenCookie);  // Add cookie to response

                return ResponseEntity.ok("Đăng nhập thành công");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không hợp lệ");
    }


    @PostMapping("/admin/create")
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

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
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


    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody UserUpdateDTO userUpdateDTO,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
//        User user = userService.getCurrentUser(session, token);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");
//        }
//        //Coach or Instructor
//        if (user.getRole() > 1 && !Objects.equals(user.getId(), userUpdateDTO.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Huấn luyện viên và hướng dẫn viên chỉ có thể cập nhật thông tin của mình.");
//        }
//        //Manager or Club Head
//        else{
//            if (user.getRole() >= userUpdateDTO.getRole() && !Objects.equals(user.getId(), userUpdateDTO.getId())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chủ nhiệm và quản lý cơ sở có thể cập nhật thông tin bản thân hoặc người dưới quyền.");
//            }
//        }

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

//        if(user.getRole() == 1 && userService.isManagerOfFacility(user.getId(), userUpdateDTO.getFacilityId())){
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Bạn không phải quản lý của cơ sở này.");
//        }
        try {
            userService.update(userUpdateDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Cập nhật người dùng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }

    @PostMapping("/admin/bulk-create")
    public ResponseEntity<?> bulkCreate(
            @Validated @RequestBody List<UserCreateDTO> userList,
            BindingResult bindingResult,
            HttpSession session,
            @CookieValue(value = "token", required = false) String token) throws Exception {

//        User currentUser = userService.getCurrentUser(session, token);
//        if (currentUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Hãy đăng nhập trước khi thực hiện.");
//        }
//
//        // Chỉ chủ nhiệm hoặc quản lý hệ thống mới được thêm người
//        if (currentUser.getRole() > 1) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Bạn không có quyền thêm người dùng.");
//        }

        // Kiểm tra lỗi DTO
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            List<User> createdUsers = userService.bulkCreateUsers(userList);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo người dùng: " + e.getMessage());
        }
    }

    @GetMapping("/admin/active-students-by-name")
    public ResponseEntity<?> getActiveStudentsByName(
            @AuthenticationPrincipal User searcher,
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<UserWithFacilityClass> students = userService.getActiveStudentsByName(searchKey, page, size, searcher);

            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách học viên: " + e.getMessage());
        }
    }

    @GetMapping("/admin/active-coach-instructor-by-name")
    public ResponseEntity<?> getActiveCoachInstructorByName(
            @AuthenticationPrincipal User searcher,
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<UserWithFacilityClass> users = userService.getActiveCoachInstructorByName(searchKey, page, size, searcher);

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách HLV và HDV: " + e.getMessage());
        }
    }

    @GetMapping("/admin/all-manager-options")
    public ResponseEntity<?> getAllManagerOptions() {
        try {
            return ResponseEntity.ok(userService.getAllManagersAsOptions());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi lấy thông tin các manager: " +  e.getMessage());
        }
    }

    @PutMapping("/admin/bulk-update")
    public ResponseEntity<?> bulkUpdateUsers(
            @AuthenticationPrincipal User creator,
            @RequestParam Integer classId,
            @Validated @RequestBody List<UserUpdateDTO> userList) throws Exception {
        try {
            userService.bulkUpdateUsers(userList, classId, creator);
            return ResponseEntity.ok("Đã cập nhật thông tin " + userList.size() + " người dùng.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
    }

    @PostMapping("/admin/create-member-for-class")
    public ResponseEntity<?> createMemberForClass(@AuthenticationPrincipal User user,
                                                  @RequestParam Integer classId,
                                                  @RequestBody List<UserCreateForClassDTO> userList) throws Exception {

        try {
            List<User> createdUsers = userService.createMembersForClass(userList, user, classId);
            createdUsers.forEach(u -> u.setPassword(""));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
        } catch (DuplicateUsersException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo người dùng: " + e.getMessage());
        }
    }

    @PostMapping("/admin/import")
    public ResponseEntity<?> importUsers(
            @AuthenticationPrincipal User creator,
            @RequestParam MultipartFile file,
            @RequestParam String type,
            @RequestParam Integer classId) {

        try {
            UserImportResult result = userService.importUsers(file, type, classId, creator);

            if (!result.hasErrors()) {
                return ResponseEntity.ok(result);
            }

            byte[] errorFile = UserImportErrorFileGenerator.generate(result);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=users-import-errors.xlsx")
                    // Add custom headers with statistics
                    .header("X-Total-Rows", String.valueOf(result.getTotalRows()))
                    .header("X-Success-Count", String.valueOf(result.getSuccessCount()))
                    .header("X-Failure-Count", String.valueOf(result.getFailureCount()))
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    ))
                    .body(errorFile);
        }
        catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}
