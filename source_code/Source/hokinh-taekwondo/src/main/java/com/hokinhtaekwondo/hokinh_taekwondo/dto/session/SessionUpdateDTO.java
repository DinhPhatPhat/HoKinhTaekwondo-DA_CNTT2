package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SessionUpdateDTO {

    @NotNull(message = "ID buổi học không được để trống")
    private Integer id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Size(max = 255, message = "Chủ đề không được vượt quá 255 ký tự")
    private String topic;

    @Size(max = 500, message = "Liên kết video không được vượt quá 500 ký tự")
    private String videoLink;

    @Size(max = 500, message = "Báo cáo không được vượt quá 500 ký tự")
    private String report;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 3, message = "Trạng thái không hợp lệ")
    private Integer status;
}
