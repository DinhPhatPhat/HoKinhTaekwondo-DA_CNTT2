package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class FacilityClassGeneralInfo {

    private Integer id;

    private String name;

    private String description;

    private String daysOfWeek;

    private LocalTime startHour;

    private LocalTime endHour;

    private Boolean isActive;

    private Integer studentCount;
}
