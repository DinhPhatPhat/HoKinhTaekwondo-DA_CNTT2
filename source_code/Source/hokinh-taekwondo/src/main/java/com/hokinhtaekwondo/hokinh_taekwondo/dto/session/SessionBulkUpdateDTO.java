package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SessionBulkUpdateDTO {

    @NotNull(message = "ID lớp học không được để trống")
    private Integer facilityClassId;

    @Valid
    private List<SessionUpdateDTO> sessions;
}