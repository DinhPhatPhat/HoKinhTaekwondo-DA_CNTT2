package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class FacilityClassUpdateDTO {

    @NotNull(message = "ID lớp học không được để trống")
    private Integer id;

    @Size(max = 100, message = "Tên lớp học tối đa 100 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @Pattern(regexp = "^(?:[2-8](?:,[2-8])*)?$", message = "Ngày học phải là các số từ 2 đến 8, phân cách bằng dấu phẩy (VD: 2,4,6)")
    private String daysOfWeek;

    private LocalTime startHour;

    private LocalTime endHour;

    private Boolean isActive;
}
