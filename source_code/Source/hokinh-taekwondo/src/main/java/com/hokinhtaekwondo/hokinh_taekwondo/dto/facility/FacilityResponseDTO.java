package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacilityResponseDTO {

    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String mapsLink;
    private String image;

    private String managerUserId;   // id của user quản lý
    private String managerName;     // tên của user quản lý
    private List<FacilityClass> classes;
}
