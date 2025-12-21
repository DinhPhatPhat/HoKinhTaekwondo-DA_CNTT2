package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassGeneralInfo;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateMultiDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.ValidateRole;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.time.VietNamTime;
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
    public FacilityClassGeneralInfo createFacilityClass(FacilityClassCreateDTO dto) {
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
        facilityClass.setCreatedAt(VietNamTime.nowDateTime());
        facilityClass.setUpdatedAt(VietNamTime.nowDateTime());

        return convertToFacilityClassGeneralInfo(facilityClassRepository.save(facilityClass));
    }

    // --- Update ---
    public void updateFacilityClass(Integer id, FacilityClassUpdateDTO dto, User creator) {
        FacilityClass facilityClass = facilityClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + id));
        User facilityManager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, facilityManager)) {
            throw new RuntimeException("Bạn không có quyền thay đổi thông tin lớp");
        }
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

        facilityClass.setUpdatedAt(VietNamTime.nowDateTime());
        facilityClassRepository.save(facilityClass);
    }

    public void updateClasses(List<FacilityClassUpdateMultiDTO> classes) {

        for(FacilityClassUpdateMultiDTO dto : classes) {
            FacilityClass facilityClass = facilityClassRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + dto.getId()));

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

            facilityClass.setUpdatedAt(VietNamTime.nowDateTime());
            facilityClassRepository.save(facilityClass);
        }
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

    public FacilityClassGeneralInfo convertToFacilityClassGeneralInfo(FacilityClass facilityClass) {
        FacilityClassGeneralInfo facilityClassGeneralInfo = new FacilityClassGeneralInfo();
        facilityClassGeneralInfo.setId(facilityClass.getId());
        facilityClassGeneralInfo.setName(facilityClass.getName());
        facilityClassGeneralInfo.setDescription(facilityClass.getDescription());
        facilityClassGeneralInfo.setDaysOfWeek(facilityClass.getDaysOfWeek());
        facilityClassGeneralInfo.setStartHour(facilityClass.getStartHour());
        facilityClassGeneralInfo.setEndHour(facilityClass.getEndHour());
        facilityClassGeneralInfo.setIsActive(facilityClass.getIsActive());
        return facilityClassGeneralInfo;
    }
}
