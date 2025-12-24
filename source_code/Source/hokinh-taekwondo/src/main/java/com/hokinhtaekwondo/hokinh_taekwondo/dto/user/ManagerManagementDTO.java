package com.hokinhtaekwondo.hokinh_taekwondo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ManagerManagementDTO {
    private String id;
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String password = "";
    private String avatar = "";
    private List<String> facilityNames = new ArrayList<>();
}
