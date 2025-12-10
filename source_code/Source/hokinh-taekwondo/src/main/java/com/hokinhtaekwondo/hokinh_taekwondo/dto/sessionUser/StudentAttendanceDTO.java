package com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentAttendanceDTO {
    @NotNull(message = "Mã võ sinh không được để trống")
    private String studentId;
    private String studentName;
    private String review;
    @NotNull(message = "Trạng thái điểm danh không được để trống")
    private Boolean attended;
}
