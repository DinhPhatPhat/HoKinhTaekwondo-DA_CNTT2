package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
public class UserWithFacilityClass {
    private String id;
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String avatar;
    private Integer role;
    private String beltLevel;
    private Integer facilityId;
    private Boolean isActive;
    private String password;
    private Timestamp createdAt;
    // In Class
    private Integer classId;
}
