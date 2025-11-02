package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Equipment;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.EquipmentService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ValidateService;
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
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody EquipmentCreateDTO dto,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hãy đăng nhập trước khi thực hiện.");
        }
        if (user.getRole() > 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền tạo thiết bị.");
        }

        // Nếu là quản lý, phải là quản lý của cơ sở đó
        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), dto.getFacilityId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không quản lý cơ sở này.");
        }

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            equipmentService.createEquipment(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đã tạo thiết bị " + dto.getName() + " cho cơ sở " + dto.getFacilityId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo thiết bị: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody EquipmentUpdateDTO dto,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hãy đăng nhập.");
        }
        if (user.getRole() > 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền cập nhật thiết bị.");
        }
        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), dto.getFacilityId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không quản lý cơ sở này.");
        }

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            equipmentService.updateEquipment(id, dto);
            return ResponseEntity.ok("Đã cập nhật thiết bị " + dto.getName());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy thiết bị có ID = " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi cập nhật thiết bị: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id,
                                    HttpSession session,
                                    @CookieValue(value = "token", required = false) String token) throws Exception {
        User user = userService.getCurrentUser(session, token);
        if (user == null || user.getRole() > 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền xóa thiết bị.");
        }

        Equipment equipment = equipmentService.getById(id);
        if (equipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy thiết bị có id = " + id);
        }

        // Nếu là quản lý, phải kiểm tra xem thiết bị đó có thuộc cơ sở của mình hay không
        if (user.getRole() == 1 && !userService.isManagerOfFacility(user.getId(), equipment.getFacility().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không quản lý cơ sở chứa thiết bị này.");
        }

        try {
            equipmentService.deleteEquipment(id);
            return ResponseEntity.ok("Đã xóa thiết bị có ID = " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi xóa thiết bị: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getEquipments(HttpSession session,@CookieValue(value = "token", required = false) String token) throws Exception{
        User user = userService.getCurrentUser(session, token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập.");
        }
        else if (user.getRole() == 0) {
            List<Equipment> equipments = equipmentService.getAllEquipments();
            return ResponseEntity.ok(equipments);
        }
        else if (user.getRole() == 1) {
            List<Equipment> equipments = equipmentService.getEquipmentsByManagerId(user.getId());
            return ResponseEntity.ok(equipments);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Bạn không có quyền xem các thiết bị.");
        }
    }
}
