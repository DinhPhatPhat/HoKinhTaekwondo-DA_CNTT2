package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacilityClassUserBulkCreateDTO {
    @NotNull(message = "Lớp không được để trống")
    private Integer facilityClassId;

    @NotEmpty(message = "Danh sách người dùng không được để trống")
    private List<UserInClassDTO> users;
}
