package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeFirstPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
