package com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCreateDTO {

    @NotNull(message = "Cơ sở không được để trống")
    private Integer facilityId;

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(max = 255, message = "Tên thiết bị không được vượt quá 255 ký tự")
    private String name;

    @NotNull(message = "Số lượng thiết bị hoạt động tốt không được để trống")
    private Integer goodQuantity;

    @NotNull(message = "Số lượng thiết bị cần sửa chữa không được để trống")
    private Integer fixableQuantity;

    @NotNull(message = "Số lượng thiết bị hư không được để trống")
    private Integer damagedQuantity;

    @Size(max = 2000, message = "Mô tả cho trạng thái tốt không được vượt quá 2000 ký tự")
    private String goodDescription;

    @Size(max = 2000, message = "Mô tả cho trạng thái cần sửa không được vượt quá 2000 ký tự")
    private String fixableDescription;

    @Size(max = 2000, message = "Mô tả cho trạng thái hư không được vượt quá 2000 ký tự")
    private String damagedDescription;

    private String unit;
}
