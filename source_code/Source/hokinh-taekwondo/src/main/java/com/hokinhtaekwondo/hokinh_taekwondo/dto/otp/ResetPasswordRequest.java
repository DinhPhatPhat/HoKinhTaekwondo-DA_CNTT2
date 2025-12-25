package com.hokinhtaekwondo.hokinh_taekwondo.dto.otp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String email;
    private String newPassword;
}
