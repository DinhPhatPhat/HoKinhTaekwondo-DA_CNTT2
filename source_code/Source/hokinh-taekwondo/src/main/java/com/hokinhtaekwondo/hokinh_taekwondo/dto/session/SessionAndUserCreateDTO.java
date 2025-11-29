package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.FullSessionUserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SessionAndUserCreateDTO {
    @NotNull(message = "Ngày buổi học không được để null")
    @NotBlank(message = "Ngày buổi học không được để trống")
    private String date;
    @NotNull(message = "Giờ bắt đầu buổi học không được để null")
    @NotBlank(message = "Giờ bắt đầu buổi học không được để trống")
    private String startTime;
    @NotNull(message = "Giờ kết thúc buổi học không được để null")
    @NotBlank(message = "Giờ kết thúc buổi học không được để trống")
    private String endTime;
    @NotNull(message = "Trạng thái buổi học không được để null")
    private Integer status;
    @NotNull(message = "Lớp cho buổi học không được để null")
    private Integer classId;
    private String topic;
    private String videoLink;
    private String report;
    private List<FullSessionUserDTO> mainInstructors;
    private List<FullSessionUserDTO> students;
}
