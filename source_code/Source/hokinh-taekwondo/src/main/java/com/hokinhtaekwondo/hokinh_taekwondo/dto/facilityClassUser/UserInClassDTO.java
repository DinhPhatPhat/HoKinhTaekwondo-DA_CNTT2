package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInClassDTO {

    @NotNull(message = "Người dùng không được để trống")
    private String userId;

    @Size(max = 50, message = "Vai trò tối đa 50 ký tự")
    private String roleInClass;

    private Boolean isActive;
}
