package com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentDTO {

    private Integer id;
    private String name;
    private Integer facilityId;

    private Integer damagedQuantity;
    private Integer fixableQuantity;
    private Integer goodQuantity;

    private String damagedDescription;
    private String fixableDescription;
    private String goodDescription;

    private String unit;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
