package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private UserLoginResponse userInfo;
}
