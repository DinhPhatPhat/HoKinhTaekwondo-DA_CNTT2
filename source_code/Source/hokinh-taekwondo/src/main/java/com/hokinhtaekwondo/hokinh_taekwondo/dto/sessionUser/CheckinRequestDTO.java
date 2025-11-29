package com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckinRequestDTO {
    private Integer sessionId;
    private double latitude;
    private double longitude;
}