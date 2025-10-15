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
    private String phone;
    private String note;
    private String mapsLink;
    private String img;

    private String managerUserId;   // id của user quản lý
    private String managerName;     // tên của user quản lý
    private List<FacilityClass> classes;
}
