package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityResponseDTO {

    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String note;

    private String managerUserId;   // id của user quản lý
    private String managerName;     // tên của user quản lý
}
