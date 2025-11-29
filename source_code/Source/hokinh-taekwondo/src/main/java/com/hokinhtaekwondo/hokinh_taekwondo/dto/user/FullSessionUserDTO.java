package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullSessionUserDTO {
    private Integer id;
    @NotBlank(message = "ID người dùng trong buổi học không được để trống")
    @NotNull(message = "ID người dùng trong buổi học không được để null")
    private String userId;
    private String name;
    @NotBlank(message = "Vai trò trong buổi học không được để trống")
    @NotNull(message = "Vai trò trong buổi học không được để null")
    private String roleInSession;
    private String review;
    private Boolean attended;
    private Integer classId;
    private String checkinTime ;
}
