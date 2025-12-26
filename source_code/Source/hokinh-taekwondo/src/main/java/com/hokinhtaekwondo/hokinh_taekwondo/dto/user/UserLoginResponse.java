package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String name;
    private Integer role;
    private Boolean isFirstChangePassword;
}
