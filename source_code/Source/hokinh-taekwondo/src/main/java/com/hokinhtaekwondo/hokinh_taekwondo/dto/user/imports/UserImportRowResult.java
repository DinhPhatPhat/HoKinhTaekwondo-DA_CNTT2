package com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserImportRowResult {

    private final int rowNumber;
    private final String userId;
    private final String fullName;
    private final String dateOfBirth;
    private final String beltLevel;
    private final String address;
    private final String phoneNumber;
    private final String error;

    public boolean isSuccess() {
        return error == null;
    }

}

