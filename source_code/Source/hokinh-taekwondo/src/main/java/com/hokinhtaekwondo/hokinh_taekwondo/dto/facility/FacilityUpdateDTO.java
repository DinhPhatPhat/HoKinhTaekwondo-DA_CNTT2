package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FacilityUpdateDTO {

    @NotNull(message = "ID cơ sở không được để trống")
    private Integer id;

    @Size(max = 200, message = "Tên tối đa 200 ký tự")
    private String name;

    @Size(max = 400, message = "Địa chỉ tối đa 400 ký tự")
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải đủ 10 số")
    private String phoneNumber;

    private String description;

    private Boolean isActive;

    private String mapsLink;

    @DecimalMin(value = "-90.0", message = "Vĩ độ không hợp lệ")
    @DecimalMax(value = "90.0", message = "Vĩ độ không hợp lệ")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Kinh độ không hợp lệ")
    @DecimalMax(value = "180.0", message = "Kinh độ không hợp lệ")
    private BigDecimal longitude;

    private String image;

    // ID của user quản lý (không cần full User object)
    private String managerUserId;
}
