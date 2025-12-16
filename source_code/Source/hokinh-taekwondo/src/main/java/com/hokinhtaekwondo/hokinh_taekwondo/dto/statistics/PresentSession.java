package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PresentSession {
    private String roleInSession;
    private Integer facilityId;
    private Integer classId;
    private LocalDate date;
    private Long lateMinutes;
}
