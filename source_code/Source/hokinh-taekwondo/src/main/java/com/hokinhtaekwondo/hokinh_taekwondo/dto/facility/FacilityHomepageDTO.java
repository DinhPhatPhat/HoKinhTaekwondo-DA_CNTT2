package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacilityHomepageDTO {
    private String address;
    private List<Schedule> schedule;
    private String name;
    private String personInCharge;
    private String phoneNumber;
    private String mapsLink;
    private String img;
}
