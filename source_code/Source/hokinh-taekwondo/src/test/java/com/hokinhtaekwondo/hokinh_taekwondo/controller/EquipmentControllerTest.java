package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.EquipmentService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(value = EquipmentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipmentService equipmentService;

    @MockitoBean
    private ValidateService validateService;

    // Cần mock cả UserService vì nó được Autowired trong Controller
    @MockitoBean
    private com.hokinhtaekwondo.hokinh_taekwondo.service.UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private EquipmentCreateDTO createDTO;
    private EquipmentUpdateDTO updateDTO;
    private EquipmentDTO equipmentDTO;

    @BeforeEach
    void setUp() {
        createDTO = new EquipmentCreateDTO();
        createDTO.setName("Võ phục");
        createDTO.setGoodQuantity(10);
        createDTO.setFixableQuantity(0);
        createDTO.setDamagedQuantity(0);

        updateDTO = new EquipmentUpdateDTO();
        updateDTO.setId(1);
        updateDTO.setName("Võ phục cập nhật");
        updateDTO.setGoodQuantity(5);
        updateDTO.setFixableQuantity(2);
        updateDTO.setDamagedQuantity(1);

        equipmentDTO = new EquipmentDTO();
        equipmentDTO.setId(1);
        equipmentDTO.setName("Võ phục");
    }

    // --- TEST POST /admin/create ---

    @Test
    void testCreate_Success() throws Exception {
        Mockito.when(validateService.checkBindingResult(any())).thenReturn(null);
        Mockito.when(equipmentService.createEquipment(any(), any())).thenReturn(equipmentDTO);

        mockMvc.perform(post("/api/equipment/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Võ phục"));
    }

    @Test
    void testCreate_ValidationError() throws Exception {
        // Giả lập validateService trả về lỗi 400
        Mockito.doReturn(ResponseEntity.badRequest().body("Dữ liệu không hợp lệ"))
                .when(validateService).checkBindingResult(any());

        mockMvc.perform(post("/api/equipment/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Dữ liệu không hợp lệ"));
    }

    @Test
    void testCreate_CatchException() throws Exception {
        Mockito.when(validateService.checkBindingResult(any())).thenReturn(null);
        Mockito.when(equipmentService.createEquipment(any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/equipment/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi tạo thiết bị")));
    }

    // --- TEST PUT /admin/update-equipments ---

    @Test
    void testUpdateEquipments_Success() throws Exception {
        List<EquipmentUpdateDTO> list = Collections.singletonList(updateDTO);
        Mockito.when(validateService.checkBindingResult(any())).thenReturn(null);

        mockMvc.perform(put("/api/equipment/admin/update-equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Đã cập nhật1thiết bị")));
    }

    @Test
    void testUpdateEquipments_CatchException() throws Exception {
        Mockito.when(validateService.checkBindingResult(any())).thenReturn(null);
        Mockito.doThrow(new RuntimeException("Update failed"))
                .when(equipmentService).updateEquipments(anyList(), any());

        mockMvc.perform(put("/api/equipment/admin/update-equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(updateDTO))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi cập nhật thiết bị")));
    }
    @Test
    void testUpdateEquipments_ValidationError() throws Exception {
        // 1. Chuẩn bị dữ liệu mẫu (List DTO)
        List<EquipmentUpdateDTO> list = Collections.singletonList(updateDTO);

        // 2. Giả lập validateService trả về lỗi (nhánh errorResponse != null)
        // Sử dụng doReturn để tránh lỗi Generic ResponseEntity<?>
        Mockito.doReturn(ResponseEntity.badRequest().body("Danh sách thiết bị không hợp lệ"))
                .when(validateService).checkBindingResult(any());

        // 3. Thực hiện request
        mockMvc.perform(put("/api/equipment/admin/update-equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                // 4. Kiểm tra kết quả: Phải trả về 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Danh sách thiết bị không hợp lệ"));

        // 5. Xác nhận logic: equipmentService.updateEquipments KHÔNG được phép gọi
        Mockito.verify(equipmentService, Mockito.never()).updateEquipments(anyList(), any());
    }
    // --- TEST DELETE /admin/delete/{id} ---

    @Test
    void testDelete_Success() throws Exception {
        Mockito.doNothing().when(equipmentService).deleteEquipment(anyInt(), any());

        mockMvc.perform(delete("/api/equipment/admin/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Đã xóa thiết bị có ID = 1"));
    }

    @Test
    void testDelete_CatchException() throws Exception {
        Mockito.doThrow(new RuntimeException("Delete error"))
                .when(equipmentService).deleteEquipment(anyInt(), any());

        mockMvc.perform(delete("/api/equipment/admin/delete/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi xóa thiết bị")));
    }

    // --- TEST GET /admin ---

    @Test
    void testGetEquipments_Success() throws Exception {
        Mockito.when(equipmentService.getAllEquipments(any()))
                .thenReturn(Collections.singletonList(equipmentDTO));

        mockMvc.perform(get("/api/equipment/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Võ phục"));
    }
}