package com.hokinhtaekwondo.hokinh_taekwondo.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    private String day;
    private List<String> shift;
}
