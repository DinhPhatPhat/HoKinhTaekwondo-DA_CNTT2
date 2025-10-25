package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateDTO {
    @NotBlank(message = "ID không được để trống")
    @Size(max = 100, message = "ID tối đa 100 ký tự")
    private String id;

    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải đủ 10 số")
    private String phoneNumber;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate dateOfBirth;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, message = "Mật khẩu ít nhất 6 ký tự")
    private String password;

    private Boolean isActive;

    @Min(value = 0, message = "Vai trò không hợp lệ")
    @Max(value = 4, message = "Vai trò không hợp lệ")
    private Integer role;

    private String beltLevel;
    private String avatar;

    private Integer facilityId;
}
