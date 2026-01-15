package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityManagementDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.service.FacilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(value = FacilityController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityService facilityService;

    @Autowired
    private ObjectMapper objectMapper;

    private FacilityRequestDTO requestDTO;
    private FacilityUpdateDTO updateDTO;
    private FacilityManagementDTO managementDTO;
    @BeforeEach
    void setUp() {
        requestDTO = new FacilityRequestDTO();
        requestDTO.setName("Cơ sở Quận 1");

        updateDTO = new FacilityUpdateDTO();
        updateDTO.setName("Cơ sở Quận 1 Updated");

        managementDTO = new FacilityManagementDTO();
        managementDTO.setId(1);
        managementDTO.setName("Cơ sở Quận 1");
    }

    // --- TEST CREATE ---

    @Test
    void testCreate_Success() throws Exception {
        // SỬA LỖI ClassCastException: Trả về đúng đối tượng DTO thay vì new Object()
        Mockito.when(facilityService.createFacility(any(), any())).thenReturn(managementDTO);

        mockMvc.perform(post("/api/facility/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cơ sở Quận 1"));
    }
    @Test
    void testCreate_ValidationError() throws Exception {
        // Gửi body trống để kích hoạt BindingResult.hasErrors()
        FacilityRequestDTO invalidDto = new FacilityRequestDTO();
        // Giả sử name có @NotBlank, việc không set name sẽ gây lỗi

        mockMvc.perform(post("/api/facility/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_InternalServerError() throws Exception {
        Mockito.when(facilityService.createFacility(any(), any()))
                .thenThrow(new RuntimeException("Lỗi DB"));

        mockMvc.perform(post("/api/facility/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi tạo cơ sở")));
    }

    // --- TEST UPDATE ---

    @Test
    void testUpdate_Success() throws Exception {
        Mockito.doNothing().when(facilityService).updateFacility(anyInt(), any(), any());

        mockMvc.perform(put("/api/facility/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cơ sở Quận 1 Updated"));
    }

    // TEST KHỐI CATCH RUNTIMEEXCEPTION (Phủ dòng màu xanh 404) ---
    @Test
    void testUpdate_NotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Entity not found"))
                .when(facilityService).updateFacility(eq(1), any(), any());

        mockMvc.perform(put("/api/facility/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Không tìm thấy cơ sở")));
    }

    // --. TEST KHỐI CATCH EXCEPTION (Phủ dòng màu đỏ 500) ---
    @Test
    void testUpdate_InternalServerError() throws Exception {

        Mockito.doAnswer(invocation -> {
            throw new Exception("Lỗi hệ thống nghiêm trọng");
        }).when(facilityService).updateFacility(anyInt(), any(), any());

        mockMvc.perform(put("/api/facility/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi hệ thống khi cập nhật cơ sở")));
    }

    // --- TEST GET ENDPOINTS ---

    @Test
    void testGetFacilitiesHomepage() throws Exception {
        Mockito.when(facilityService.getAllFacilitiesForHomepage()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/facility/homepage"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFacilitiesForWebsiteManagement() throws Exception {
        Mockito.when(facilityService.getAllFacilitiesForWebsiteManagement(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/facility/admin/website-management"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFacilitiesForManagement() throws Exception {
        Mockito.when(facilityService.getAllFacilitiesForManagement(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/facility/admin/management"))
                .andExpect(status().isOk());
    }





}
