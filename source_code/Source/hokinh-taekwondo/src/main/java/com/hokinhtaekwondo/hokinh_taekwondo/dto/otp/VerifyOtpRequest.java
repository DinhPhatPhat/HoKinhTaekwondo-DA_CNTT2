package com.hokinhtaekwondo.hokinh_taekwondo.dto.otp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
