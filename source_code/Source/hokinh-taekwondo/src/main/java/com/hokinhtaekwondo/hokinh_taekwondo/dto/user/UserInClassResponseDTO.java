// UserInClassResponseDTO.java
package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserInClassResponseDTO {
    private String id;
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String avatar;
    private Integer role;
    private String beltLevel;
    private Integer facilityId;
    // In Class
    private Integer classId;
    private Boolean isActiveInClass;
    private String roleInClass;

}
