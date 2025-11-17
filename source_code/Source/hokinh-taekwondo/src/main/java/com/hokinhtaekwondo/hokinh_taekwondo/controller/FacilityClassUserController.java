package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.FacilityClassUserBulkCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.FacilityClassUserBulkUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.FacilityClassUserCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.FacilityClassUserUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserInClassResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClassUser;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;

@RestController
@RequestMapping("/api/facility-class-user")
public class FacilityClassUserController {

    @Autowired
    private FacilityClassUserService facilityClassUserService;
    @Autowired
    private FacilityClassService facilityClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody FacilityClassUserCreateDTO dto,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hãy đăng nhập trước khi thực hiện.");

        if (user.getRole() > 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thêm người vào lớp.");

        FacilityClass facilityClass = facilityClassService.getById(dto.getFacilityClassId());
        if (facilityClass == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tồn tại lớp.");
        }

        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), facilityClass.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không quản lý lớp học này.");
        }
        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            facilityClassUserService.createFacilityClassUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã thêm người dùng " + dto.getUserId() + " vào lớp " + dto.getFacilityClassId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi thêm người vào lớp: " + e.getMessage());
        }
    }

    // 🟡 Cập nhật vai trò hoặc trạng thái trong lớp
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody FacilityClassUserUpdateDTO dto,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hãy đăng nhập.");

        if (user.getRole() > 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền chỉnh sửa thông tin lớp.");

        FacilityClassUser facilityClassUser = facilityClassUserService.getById(id);
        if (facilityClassUser == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng trong lớp muốn cập nhật");

        // Quản lý chỉ được sửa các lớp thuộc cơ sở mình quản lý
        if (user.getRole() == 1 &&
                 !userService.isManagerOfFacility(user.getId(),facilityClassUser.getFacilityClass().getFacility().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không quản lý cơ sở của lớp học này.");
        }

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            facilityClassUserService.updateFacilityClassUser(id, dto);
            return ResponseEntity.ok("Đã cập nhật thông tin người trong lớp.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi cập nhật: " + e.getMessage());
        }
    }

    //  Xóa (gỡ user khỏi lớp)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null || user.getRole() > 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xóa người khỏi lớp.");

        FacilityClassUser record = facilityClassUserService.getById(id);
        if (record == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bản ghi có ID = " + id);
        FacilityClassUser facilityClassUser = facilityClassUserService.getById(id);
        // Quản lý chỉ được sửa các lớp thuộc cơ sở mình quản lý
        if (user.getRole() == 1 &&
                !userService.isManagerOfFacility(user.getId(),facilityClassUser.getFacilityClass().getFacility().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không quản lý cơ sở của lớp học này.");
        }

        try {
            facilityClassUserService.deleteFacilityClassUser(id);
            return ResponseEntity.ok("Đã xóa người khỏi lớp.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi xóa: " + e.getMessage());
        }
    }

    // Lấy danh sách người dùng còn hoạt động trong lớp
    @GetMapping("/active/{classId}")
    public ResponseEntity<?> getActiveUsersByClass(@PathVariable Integer classId) {
        try {
            List<UserInClassResponseDTO> users = facilityClassUserService.getActiveUsersByClassId(classId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi lấy danh sách người trong lớp: " + e.getMessage());
        }
    }

    // Lấy danh sách người dùng không còn hoạt động trong lớp
    @GetMapping("/in-active/{classId}")
    public ResponseEntity<?> getInActiveUsersByClass(@PathVariable Integer classId) {
        try {
            List<UserInClassResponseDTO> users = facilityClassUserService.getInActiveUsersByClassId(classId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi lấy danh sách người trong lớp: " + e.getMessage());
        }
    }


    @PostMapping("/bulk-create")
    public ResponseEntity<?> bulkCreate(
            @Validated @RequestBody FacilityClassUserBulkCreateDTO dto,
            BindingResult bindingResult,
            HttpSession session,
            @CookieValue(value = "token", required = false) String token) throws Exception {

        // --- Kiểm tra đăng nhập ---
//        User user = userService.getCurrentUser(session, token);
//        if (user == null)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Hãy đăng nhập trước khi thực hiện.");
//
//        // --- Chặn role không có quyền ---
//        if (user.getRole() > 1)
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Bạn không có quyền thêm người vào lớp.");

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            facilityClassUserService.bulkCreate(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã thêm " + dto.getUsers().size() + " người dùng vào lớp " + dto.getFacilityClassId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi thêm người vào lớp: " + e.getMessage());
        }
    }

    @PutMapping("/bulk-update")
    public ResponseEntity<?> bulkUpdate(
            @Validated @RequestBody FacilityClassUserBulkUpdateDTO dto,
            BindingResult bindingResult,
            HttpSession session,
            @CookieValue(value = "token", required = false) String token) throws Exception {

        // --- Kiểm tra đăng nhập ---
//        User user = userService.getCurrentUser(session, token);
//        if (user == null)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Hãy đăng nhập trước khi thực hiện.");
//
//        // --- Chặn role không có quyền ---
//        if (user.getRole() > 1)
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Bạn không có quyền thêm người vào lớp.");

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            facilityClassUserService.bulkUpdate(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã sửa " + dto.getUsers().size() + " người dùng vào lớp " + dto.getFacilityClassId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi thêm người vào lớp: " + e.getMessage());
        }
    }
}
