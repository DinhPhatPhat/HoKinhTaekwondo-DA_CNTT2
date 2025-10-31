package com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentUpdateDTO {

    @NotNull(message = "Cơ sở không được để trống")
    private Integer facilityId;

    @Size(max = 255, message = "Tên thiết bị không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;

    private String status;
}
