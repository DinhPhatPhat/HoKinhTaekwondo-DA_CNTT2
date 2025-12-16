package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.StudentAttendanceDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class InstructorSessionDTO {
    private Integer id;
    private String className;
    private String facilityName;
    private String facilityAddress;
    private String facilityMapsLink;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
    private String topic;
    private String report;
    private String videoLink;
    private Boolean attended;
    private String role;
    private LocalDateTime checkinTime;
    private List<StudentAttendanceDTO> students;
}
