package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityCreateDTO {
    @NotBlank(message = "Tên cơ sở không được để trống")
    @Size(max = 200, message = "Tên cơ sở tối đa 200 ký tự")
    private String name;

    @Size(max = 400, message = "Địa chỉ tối đa 400 ký tự")
    private String address;

    @Size(max = 10, message = "Số điện thoại tối đa 10 ký tự")
    private String phoneNumber;

    private String description;

    private String managerUserId; // id của user quản lý (nếu có)
}
