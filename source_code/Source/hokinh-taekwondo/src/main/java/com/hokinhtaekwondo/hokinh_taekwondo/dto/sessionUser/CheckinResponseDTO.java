package com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckinResponseDTO {
    private String message;
    private LocalDateTime checkinTime;
}
