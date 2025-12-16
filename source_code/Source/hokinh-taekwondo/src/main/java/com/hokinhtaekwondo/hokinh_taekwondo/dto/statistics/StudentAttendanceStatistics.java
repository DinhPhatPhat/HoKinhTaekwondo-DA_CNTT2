package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentAttendanceStatistics {
    private String userId;
    private String studentName;
    private Integer classId;
    private Boolean isActive;
    private Integer numAttendedSession = 0;
    private Integer numAbsentSession = 0;
}
