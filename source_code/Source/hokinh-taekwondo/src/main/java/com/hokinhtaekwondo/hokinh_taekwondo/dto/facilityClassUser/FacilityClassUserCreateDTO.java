package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityClassUserCreateDTO {

    @NotNull(message = "ID lớp học không được để trống")
    private Integer facilityClassId;

    @NotNull(message = "ID người dùng không được để trống")
    private String userId;

    @NotBlank(message = "Vai trò trong lớp không được để trống")
    @Size(max = 50, message = "Vai trò tối đa 50 ký tự")
    private String roleInClass;

    private Boolean isActive = true;
}
