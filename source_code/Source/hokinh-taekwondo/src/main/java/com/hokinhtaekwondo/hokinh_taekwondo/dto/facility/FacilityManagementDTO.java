package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassGeneralInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FacilityManagementDTO {
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String mapsLink;
    private String image;
    private String managerName;
    private String managerId;
    private Boolean isActive;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<FacilityClassGeneralInfo> classes;
}
