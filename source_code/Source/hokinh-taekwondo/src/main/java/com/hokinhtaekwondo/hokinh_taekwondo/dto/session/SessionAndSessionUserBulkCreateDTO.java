package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.SessionUserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class SessionAndSessionUserBulkCreateDTO {

//    facilityClassId;
//    startDate;
//    endDate;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endTime;

    @NotNull(message = "Thứ không được bỏ trống")
    private Integer dayOfWeek;

    @NotNull
    private List<SessionUserDTO> users;

}
