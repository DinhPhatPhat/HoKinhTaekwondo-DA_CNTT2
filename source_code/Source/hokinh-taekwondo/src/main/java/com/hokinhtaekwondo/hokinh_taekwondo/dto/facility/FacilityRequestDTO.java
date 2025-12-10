package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FacilityRequestDTO {

    @NotBlank(message = "Tên cơ sở không được để trống")
    @Size(max = 200, message = "Tên cơ sở tối đa 200 ký tự")
    private String name;

    @Size(max = 400, message = "Địa chỉ tối đa 400 ký tự")
    private String address;

    @Size(max = 10, message = "Số điện thoại tối đa 10 ký tự")
    private String phoneNumber;

    @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
    private String description;

    private String managerUserId; // id của user quản lý (nếu có)

    private String mapsLink;

    private String image;

    @DecimalMin(value = "-180.0", message = "Kinh độ phải từ -180 đến 180")
    @DecimalMax(value = "180.0", message = "Kinh độ phải từ -180 đến 180")
    private BigDecimal longitude;

    @DecimalMin(value = "-90.0", message = "Vĩ độ phải từ -90 đến 90")
    @DecimalMax(value = "90.0", message = "Vĩ độ phải từ -90 đến 90")
    private BigDecimal latitude;

    private Boolean isActive;
}
