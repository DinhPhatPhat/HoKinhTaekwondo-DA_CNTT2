// UserResponseDTO.java
package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDTO {
    private String id;
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String avatar;
    private String role;
    private Boolean isActive;
    private String beltLevel;
    private Integer facilityId;
}
