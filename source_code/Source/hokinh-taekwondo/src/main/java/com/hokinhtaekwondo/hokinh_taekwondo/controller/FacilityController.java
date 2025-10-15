package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityHomepageDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.Schedule;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/homepage")
    public ResponseEntity<?> getFacilitiesHomepage() {
        List<FacilityResponseDTO> facilities = facilityService.getAllFacilities();
        List<FacilityHomepageDTO> displayedFacilities = new ArrayList<>();
        for(FacilityResponseDTO facility : facilities) {
            HashMap<String, Schedule> schedules = new HashMap<>();
            for(FacilityClass facilityClass : facility.getClasses()) {
                List<String> hours = new ArrayList<>();

                if(schedules.get(facilityClass.getDays()) != null) {
                    hours = schedules.get(facilityClass.getDays()).getShift();
                    hours.add(facilityClass.getStartHour() + "-" + facilityClass.getEndHour());
                    schedules.get(facilityClass.getDays()).setShift(hours);
                }
                else {
                    hours.add(facilityClass.getStartHour() + "-" + facilityClass.getEndHour());
                    schedules.put(facilityClass.getDays(), new Schedule(facilityClass.getDays(), hours));
                }
            }
            displayedFacilities.add(new FacilityHomepageDTO(
                    facility.getAddress(),
                    new ArrayList<>(schedules.values()),
                    facility.getMapsLink(),
                    facility.getImg()
            ));
        }
        return ResponseEntity.ok(displayedFacilities);
    }

    @GetMapping("/all_facilities")
    public ResponseEntity<?> getAllFacilities() {
        return  ResponseEntity.ok(facilityService.getAllFacilities());
    }
}
