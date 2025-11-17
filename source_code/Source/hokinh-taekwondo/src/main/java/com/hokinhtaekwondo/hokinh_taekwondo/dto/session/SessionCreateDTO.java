package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SessionCreateDTO {

    @NotNull(message = "Lớp học không được để trống")
    private Integer facilityClassId;

    @NotNull(message = "Ngày học không được để trống")
    private LocalDate date;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endTime;

    @NotBlank(message = "Chủ đề buổi học không được để trống")
    private String topic;

    @Size(max = 500, message = "Liên kết video không được vượt quá 500 ký tự")
    private String videoLink;

    @Size(max = 500, message = "Báo cáo không được vượt quá 500 ký tự")
    private String report;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 3, message = "Trạng thái không hợp lệ")
    private Integer status = 0; // 0: planned
}
