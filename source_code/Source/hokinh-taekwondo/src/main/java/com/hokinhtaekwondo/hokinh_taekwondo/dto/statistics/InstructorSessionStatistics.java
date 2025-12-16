package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InstructorSessionStatistics {
    private String userId;
    private String userName;
    private List<AbsentSession>  absentSessions = new ArrayList<>();
    private List<PresentSession> presentSessions = new ArrayList<>();
}
