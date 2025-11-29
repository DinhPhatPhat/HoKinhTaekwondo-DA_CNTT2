package com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentReviewDTO {

    @NotNull(message = "Buổi học không được để trống")
    private Integer sessionId;
    @NotNull(message = "Học sinh không được để trống")
    private String studentId;
    @NotNull(message = "Đánh giá học sinh không được để trống")
    private String review;
}
