package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacilityWebsiteManagementDTO {
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String mapsLink;
    private String image;

    private List<FacilityClassUpdateDTO> classes;
}
