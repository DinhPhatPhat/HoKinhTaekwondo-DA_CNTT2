package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassGeneralInfo;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateMultiDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.FacilityClassService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(value = FacilityClassController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class FacilityClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityClassService facilityClassService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private FacilityClassCreateDTO createDTO;
    private FacilityClassUpdateDTO updateDTO;
    private FacilityClass sampleClass;
    private User managerUser;

    @BeforeEach
    void setUp() {
        createDTO = new FacilityClassCreateDTO();
        createDTO.setName("Lớp Sáng");
        createDTO.setFacilityId(10);
        createDTO.setStartHour(LocalTime.of(6, 0));
        createDTO.setEndHour(LocalTime.of(7, 30));

        updateDTO = new FacilityClassUpdateDTO();
        updateDTO.setId(1);
        updateDTO.setName("Lớp Chiều");

        sampleClass = new FacilityClass();
        sampleClass.setId(1);
        Facility f = new Facility();
        f.setId(10);
        sampleClass.setFacility(f);

        managerUser = new User();
        managerUser.setId("user1");
        managerUser.setRole(1);
    }
    // --- TEST CREATE ---

    @Test
    void testCreate_Success() throws Exception {
        // Tạo đối tượng DTO trả về đúng kiểu mà Service khai báo
        FacilityClassGeneralInfo generalInfo = new FacilityClassGeneralInfo();
        generalInfo.setId(1);
        generalInfo.setName("Lớp Võ Sáng");

        // Mock service trả về generalInfo thay vì sampleClass
        Mockito.when(facilityClassService.createFacilityClass(any(FacilityClassCreateDTO.class)))
                .thenReturn(generalInfo);

        mockMvc.perform(post("/api/facility-class/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Lớp Võ Sáng"));
    }

    @Test
    void testCreate_InternalServerError() throws Exception {
        Mockito.when(facilityClassService.createFacilityClass(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/facility-class/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isInternalServerError());
    }

    // --- TEST UPDATE ---

    @Test
    void testUpdate_NotFound() throws Exception {
        Mockito.when(facilityClassService.getById(1)).thenReturn(null);

        mockMvc.perform(put("/api/facility-class/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_Success() throws Exception {
        Mockito.when(facilityClassService.getById(1)).thenReturn(sampleClass);

        mockMvc.perform(put("/api/facility-class/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Đã cập nhật lớp")));
    }

    // --- TEST DELETE (Phủ các nhánh Role và Forbidden) ---

    @Test
    void testDelete_Forbidden_RoleTooHigh() throws Exception {
        User student = new User();
        student.setRole(2); // Role > 1
        Mockito.when(userService.getCurrentUser(any(), any())).thenReturn(student);

        mockMvc.perform(delete("/api/facility-class/admin/delete/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Bạn không có quyền xóa lớp")));
    }

    @Test
    void testDelete_Forbidden_NotManagerOfFacility() throws Exception {
        Mockito.when(userService.getCurrentUser(any(), any())).thenReturn(managerUser);
        Mockito.when(facilityClassService.getById(1)).thenReturn(sampleClass);
        Mockito.when(userService.isManagerOfFacility(anyString(), anyInt())).thenReturn(false);

        mockMvc.perform(delete("/api/facility-class/admin/delete/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Bạn không quản lý cơ sở")));
    }

    @Test
    void testDelete_Success() throws Exception {
        Mockito.when(userService.getCurrentUser(any(), any())).thenReturn(managerUser);
        Mockito.when(facilityClassService.getById(1)).thenReturn(sampleClass);
        Mockito.when(userService.isManagerOfFacility(anyString(), anyInt())).thenReturn(true);

        mockMvc.perform(delete("/api/facility-class/admin/delete/1"))
                .andExpect(status().isOk());
    }

    // --- TEST UPDATE MULTI ---

    @Test
    void testUpdateMulti_InternalServerError() throws Exception {
        FacilityClassUpdateMultiDTO multiDTO = new FacilityClassUpdateMultiDTO();
        multiDTO.setId(1);
        multiDTO.setName("Multi Test");
        List<FacilityClassUpdateMultiDTO> list = List.of(multiDTO);

        Mockito.doThrow(new RuntimeException("Bulk Update Fail"))
                .when(facilityClassService).updateClasses(anyList());

        mockMvc.perform(put("/api/facility-class/admin/update-classes-website-management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testUpdate_InternalServerError() throws Exception {
        Mockito.when(facilityClassService.getById(1)).thenReturn(sampleClass);

        Mockito.doThrow(new RuntimeException("Update failed"))
                .when(facilityClassService)
                .updateFacilityClass(eq(1), any(FacilityClassUpdateDTO.class), any());

        mockMvc.perform(put("/api/facility-class/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi cập nhật lớp")));
    }
    @Test
    void testDelete_InternalServerError() throws Exception {
        Mockito.when(userService.getCurrentUser(any(), any())).thenReturn(managerUser);
        Mockito.when(facilityClassService.getById(1)).thenReturn(sampleClass);
        Mockito.when(userService.isManagerOfFacility(anyString(), anyInt())).thenReturn(true);

        Mockito.doThrow(new RuntimeException("Delete failed"))
                .when(facilityClassService).deleteFacilityClass(1);

        mockMvc.perform(delete("/api/facility-class/admin/delete/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi xóa lớp")));
    }
    @Test
    void testGetFacilityClasses_Success() throws Exception {
        Mockito.when(facilityClassService.getAllFacilityClasses())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/facility-class/admin"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
    @Test
    void testUpdate_NoBindingError() throws Exception {
        Mockito.when(facilityClassService.getById(1)).thenReturn(sampleClass);

        mockMvc.perform(put("/api/facility-class/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }
    @Test
    void testUpdate_BindingError_TargetNotNull() throws Exception {
        Mockito.when(facilityClassService.getById(1))
                .thenReturn(sampleClass);

        FacilityClassUpdateDTO invalidDTO = new FacilityClassUpdateDTO();
        invalidDTO.setName("Sai DTO"); // cố tình thiếu field bắt buộc

        mockMvc.perform(put("/api/facility-class/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_MultipleBindingErrors_Order() throws Exception {
        FacilityClassCreateDTO invalidDTO = new FacilityClassCreateDTO();
        invalidDTO.setFacilityId(null);       // lỗi 1
        invalidDTO.setStartHour(null);        // lỗi 2
        invalidDTO.setEndHour(null);          // lỗi 3

        mockMvc.perform(post("/api/facility-class/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

}