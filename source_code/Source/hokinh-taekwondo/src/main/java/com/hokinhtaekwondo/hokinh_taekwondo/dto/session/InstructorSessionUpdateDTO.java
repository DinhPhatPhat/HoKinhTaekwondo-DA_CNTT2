package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.StudentAttendanceDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstructorSessionUpdateDTO {
    private Integer id;
    private String topic;
    private String report;
    private String videoLink;
    private List<StudentAttendanceDTO> students;
}
