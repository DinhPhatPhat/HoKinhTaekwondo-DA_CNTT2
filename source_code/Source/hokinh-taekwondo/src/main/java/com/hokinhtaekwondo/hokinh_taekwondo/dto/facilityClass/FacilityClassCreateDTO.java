package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class FacilityClassCreateDTO {

    @NotBlank(message = "Tên lớp học không được để trống")
    @Size(max = 100, message = "Tên lớp học tối đa 100 ký tự")
    private String name;

    @NotNull(message = "Cơ sở không được để trống")
    private Integer facilityId;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @Pattern(regexp = "^(?:[2-8](?:-[2-8])*)?$", message = "Ngày học phải là các số từ 2 đến 8, phân cách bằng dấu - (VD: 2-4-6)")
    private String daysOfWeek;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startHour;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endHour;

    private Boolean isActive = true;
}
