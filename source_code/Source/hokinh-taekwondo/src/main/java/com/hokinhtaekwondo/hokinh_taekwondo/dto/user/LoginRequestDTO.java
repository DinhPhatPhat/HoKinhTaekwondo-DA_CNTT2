package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Vui lòng nhập mã tài khoản.")
    private String id;

    @NotBlank(message = "Vui lòng nhập mật khẩu.")
    private String password;
}
