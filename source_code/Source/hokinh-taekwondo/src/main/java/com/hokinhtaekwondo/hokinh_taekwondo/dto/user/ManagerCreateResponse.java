package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ManagerCreateResponse {
    @NotBlank
    private String id;
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;
    private String phoneNumber;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate dateOfBirth;
    private String email;
    private String password = "";

    private String avatar = "";

    public ManagerCreateResponse(User user) {
        id = user.getId();
        name = user.getName();
        phoneNumber = user.getPhoneNumber();
        dateOfBirth = user.getDateOfBirth();
        email = user.getEmail();
        password = user.getPassword();
        avatar = user.getAvatar();
    }
}
