package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserManagementDTO {
    @NotBlank(message = "ID không được để trống")
    @Size(max = 100, message = "ID tối đa 100 ký tự")
    private String id;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;
    private String phoneNumber;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate dateOfBirth;
    private String email;
    private String password = "";

    private String avatar;

    private Integer role;

    private String beltLevel;

    private Integer facilityId;
}
