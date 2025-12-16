package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AbsentSession {
    private Integer classId;
    private Integer facilityId;
    private String roleInSession;
    private LocalDate date;
}
