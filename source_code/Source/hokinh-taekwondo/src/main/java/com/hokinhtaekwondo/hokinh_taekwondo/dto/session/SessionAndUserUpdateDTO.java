package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.FullSessionUserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SessionAndUserUpdateDTO {
    private Integer id;
    private String date;
    private String startTime;
    private String endTime;
    private Integer status;
    private String topic;
    private String videoLink;
    private String report;
    private List<FullSessionUserDTO> mainInstructors;
    private List<FullSessionUserDTO> students;
    private List<Integer> sessionUserIds;
}
