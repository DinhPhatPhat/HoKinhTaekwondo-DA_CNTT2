package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ManagerCreateDTO {

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;
    private String phoneNumber;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate dateOfBirth;
    private String email;
    private String password = "";

    private String avatar = "";
}

