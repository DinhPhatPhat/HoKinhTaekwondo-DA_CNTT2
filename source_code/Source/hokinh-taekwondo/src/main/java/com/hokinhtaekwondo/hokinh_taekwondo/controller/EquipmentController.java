package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Equipment;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.EquipmentService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ValidateService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;

    @PostMapping("/admin/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @Valid @RequestBody EquipmentCreateDTO dto,
                                    BindingResult bindingResult) throws Exception {


        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(equipmentService.createEquipment(dto, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo thiết bị: " + e.getMessage());
        }
    }

    @PutMapping("/admin/update-equipments")
    public ResponseEntity<?> updateEquipments(@AuthenticationPrincipal User user,
                                    @Valid @RequestBody List<EquipmentUpdateDTO> equipments,
                                    BindingResult bindingResult) throws Exception {

        ResponseEntity<?> errorResponse = validateService.checkBindingResult(bindingResult);
        if (errorResponse != null) return errorResponse;

        try {
            equipmentService.updateEquipments(equipments, user);
            return ResponseEntity.ok("Đã cập nhật" + equipments.size() + "thiết bị");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi cập nhật thiết bị: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User deleteAuthor,
                                    @PathVariable Integer id) throws Exception {

        try {
            equipmentService.deleteEquipment(id, deleteAuthor);
            return ResponseEntity.ok("Đã xóa thiết bị có ID = " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi xóa thiết bị: " + e.getMessage());
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getEquipments(@AuthenticationPrincipal User user) throws Exception{
        List<EquipmentDTO> equipments = equipmentService.getAllEquipments(user);
        return ResponseEntity.ok(equipments);
    }
}
