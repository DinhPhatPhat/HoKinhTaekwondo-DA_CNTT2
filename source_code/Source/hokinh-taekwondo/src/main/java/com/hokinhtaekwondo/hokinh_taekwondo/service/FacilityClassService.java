package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityClassService {

    private final FacilityClassRepository facilityClassRepository;
    private final FacilityRepository facilityRepository;

    // --- Create ---
    public void createFacilityClass(FacilityClassCreateDTO dto) {
        Facility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));

        FacilityClass facilityClass = new FacilityClass();
        facilityClass.setName(dto.getName());
        facilityClass.setFacility(facility);
        facilityClass.setDescription(dto.getDescription());
        facilityClass.setDaysOfWeek(dto.getDaysOfWeek());
        facilityClass.setStartHour(dto.getStartHour());
        facilityClass.setEndHour(dto.getEndHour());
        facilityClass.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        facilityClass.setCreatedAt(LocalDateTime.now());
        facilityClass.setUpdatedAt(LocalDateTime.now());

        facilityClassRepository.save(facilityClass);
    }

    // --- Update ---
    public void updateFacilityClass(Integer id, FacilityClassUpdateDTO dto) {
        FacilityClass facilityClass = facilityClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + id));

        if (StringUtils.hasText(dto.getName())) {
            facilityClass.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            facilityClass.setDescription(dto.getDescription());
        }
        if (StringUtils.hasText(dto.getDaysOfWeek())) {
            facilityClass.setDaysOfWeek(dto.getDaysOfWeek());
        }
        if (dto.getStartHour() != null) {
            facilityClass.setStartHour(dto.getStartHour());
        }
        if (dto.getEndHour() != null) {
            facilityClass.setEndHour(dto.getEndHour());
        }
        if (dto.getIsActive() != null) {
            facilityClass.setIsActive(dto.getIsActive());
        }

        facilityClass.setUpdatedAt(LocalDateTime.now());
        facilityClassRepository.save(facilityClass);
    }

    // --- Delete ---
    public void deleteFacilityClass(Integer id) {
        FacilityClass facilityClass = facilityClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + id));
        facilityClassRepository.delete(facilityClass);
    }

    // --- Find All ---
    public List<FacilityClass> getAllFacilityClasses() {
        return facilityClassRepository.findAll();
    }

    public List<FacilityClass> getFacilityClassesByManagerId(String managerId) {
        return facilityClassRepository.findAllByFacility_Manager_Id(managerId);
    }

    public FacilityClass getById(Integer id) {
        return facilityClassRepository.findById(id).orElse(null);
    }
}
