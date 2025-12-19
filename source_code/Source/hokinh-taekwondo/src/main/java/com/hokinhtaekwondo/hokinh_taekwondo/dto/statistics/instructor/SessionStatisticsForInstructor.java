package com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.AbsentSession;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.PresentSession;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class SessionStatisticsForInstructor {
    private List<AbsentSession> absentSessions = new ArrayList<>();
    private List<PresentSession> presentSessions = new ArrayList<>();
    private HashMap<Integer, FacilityInfo> facilityMap = new HashMap<>();
    private HashMap<Integer, FacilityClassInfo> classMap = new HashMap<>();
}
