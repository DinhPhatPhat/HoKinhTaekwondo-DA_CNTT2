package com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassOfStudent {
    private Integer classId;
    private String studentId;
    private Boolean isActive;

    public ClassOfStudent(Integer classId, String studentId, Boolean isActive) {
        this.classId = classId;
        this.studentId = studentId;
        this.isActive = isActive;
    }
}
