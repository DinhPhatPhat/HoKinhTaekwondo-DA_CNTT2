package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SessionStatistics {
    List<StudentAttendanceStatistics> studentAttendanceList;
    List<InstructorSessionStatistics> instructorSessionStatistics;
}
