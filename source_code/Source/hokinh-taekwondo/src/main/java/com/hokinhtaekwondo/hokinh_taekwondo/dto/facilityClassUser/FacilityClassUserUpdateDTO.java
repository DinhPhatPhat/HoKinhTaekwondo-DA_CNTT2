package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityClassUserUpdateDTO {

    private Integer facilityClassId;

    @Size(max = 50, message = "Vai trò tối đa 50 ký tự")
    private String roleInClass;

    private Boolean isActive;
}
