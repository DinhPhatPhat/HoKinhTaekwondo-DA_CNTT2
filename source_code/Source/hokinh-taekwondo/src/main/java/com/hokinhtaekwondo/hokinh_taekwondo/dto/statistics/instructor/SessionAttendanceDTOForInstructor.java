package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class SessionAttendanceDTOForInstructor {

    private String userId;
    private String roleInSession;
    private String userName;
    private LocalDate date;
    private Boolean attended;
    private LocalTime startTime;
    private LocalDateTime checkinTime;
    private Integer facilityClassId;
    private String facilityClassName;
    private Integer facilityId;
    private String facilityName;

    public SessionAttendanceDTOForInstructor(
            String userId,
            String roleInSession,
            String userName,
            LocalDate date,
            LocalTime startTime,
            LocalDateTime checkinTime,
            Boolean attended,
            Integer facilityId,
            Integer facilityClassId,
            String facilityName,
            String facilityClassName
    ) {
        this.userId = userId;
        this.roleInSession = roleInSession;
        this.userName = userName;
        this.date = date;
        this.startTime = startTime;
        this.checkinTime = checkinTime;
        this.attended = attended;
        this.facilityId = facilityId;
        this.facilityClassId = facilityClassId;
        this.facilityName = facilityName;
        this.facilityClassName = facilityClassName;
    }

    public Duration getCheckinDelay() {
        if (checkinTime == null) return null;

        LocalDateTime start =
                LocalDateTime.of(date, startTime);

        return Duration.between(start, checkinTime);
    }

}


