package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class StudentScheduleInfo {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean attended;
    private String review;
    private Integer status;
    private String className;
    private String facilityName;
    private String address;
    private String mapsLink;
}
