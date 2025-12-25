package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassGeneralInfo;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClass.FacilityClassUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassUserRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.time.VietNamTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityClassUserRepository facilityClassUserRepository;
    private final UserRepository userRepository;

    // --- Create ---
    public FacilityManagementDTO createFacility(FacilityRequestDTO dto) {
        Facility facility = new Facility();
        facility.setName(dto.getName());
        facility.setAddress(dto.getAddress());
        facility.setPhoneNumber(dto.getPhoneNumber());
        facility.setDescription(dto.getDescription());
        facility.setMapsLink(dto.getMapsLink());
        facility.setImage(dto.getImage());
        facility.setLatitude(dto.getLatitude());
        facility.setLongitude(dto.getLongitude());
        facility.setIsActive(dto.getIsActive());
        facility.setCreatedAt(VietNamTime.nowDateTime());

        // Gắn manager nếu có
        if (dto.getManagerUserId() != null) {
            Optional<User> managerOpt = userRepository.findById(dto.getManagerUserId());
            managerOpt.ifPresent(facility::setManager);
        }

        Facility newFacility = facilityRepository.save(facility);
        return toFacilityManagementDTO(newFacility);
    }

    // --- Update ---
    public void updateFacility(Integer id, FacilityUpdateDTO dto) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        // Update only non-null fields
        if (dto.getName() != null) facility.setName(dto.getName());
        if (dto.getAddress() != null) facility.setAddress(dto.getAddress());
        if (dto.getPhoneNumber() != null) facility.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDescription() != null) facility.setDescription(dto.getDescription());
        if (dto.getMapsLink() != null) facility.setMapsLink(dto.getMapsLink());
        if (dto.getImage() != null) facility.setImage(dto.getImage());
        if (dto.getLatitude() != null) facility.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) facility.setLongitude(dto.getLongitude());
        if (dto.getIsActive() != null) facility.setIsActive(dto.getIsActive());

        // Handle manager user
        if (dto.getManagerUserId() != null) {
            if(dto.getManagerUserId().isEmpty()) {
                facility.setManager(null);
            }
            else {
                userRepository.findById(dto.getManagerUserId())
                        .ifPresent(facility::setManager);
            }
        }

        facility.setUpdatedAt(VietNamTime.nowDateTime());
        facilityRepository.save(facility);
    }


    // --- Delete ---
    public void deleteFacility(Integer id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        List<User> usersInFacility = userRepository.findByFacility(facility);
        if (!usersInFacility.isEmpty()) {
            throw new RuntimeException("Không thể xóa cơ sở vì vẫn còn người dùng thuộc về cơ sở này");
        }

        facilityRepository.deleteById(id);
    }

    public List<FacilityWebsiteManagementDTO> getAllFacilitiesForWebsiteManagement(User user) {
        if(user.getRole() != 0) {
            throw new RuntimeException("Bạn không có quyền quản lý website");
        }
        return facilityRepository.findAllByIsActiveEquals(true)
                .stream()
                .map(this::toFacilityWebsiteManagementDTO)
                .collect(Collectors.toList());
    }

    public List<FacilityHomepageDTO> getAllFacilitiesForHomepage() {
        List<FacilityWebsiteManagementDTO> activeFacilities = facilityRepository.findAllByIsActiveEquals(true)
                .stream()
                .map(this::toFacilityWebsiteManagementDTO)
                .toList();
        List<FacilityHomepageDTO> displayedFacilities = new ArrayList<>();
        for(FacilityWebsiteManagementDTO facility : activeFacilities) {
            HashMap<String, Schedule> schedules = new HashMap<>();
            for(FacilityClassUpdateDTO facilityClass : facility.getClasses()) {
                List<String> hours = new ArrayList<>();

                if(schedules.get(facilityClass.getDaysOfWeek()) != null) {
                    hours = schedules.get(facilityClass.getDaysOfWeek()).getShift();
                    hours.add(facilityClass.getStartHour() + "-" + facilityClass.getEndHour());
                    schedules.get(facilityClass.getDaysOfWeek()).setShift(hours);
                }
                else {
                    hours.add(facilityClass.getStartHour() + "-" + facilityClass.getEndHour());
                    schedules.put(facilityClass.getDaysOfWeek(), new Schedule(facilityClass.getDaysOfWeek(), hours));
                }
            }
            displayedFacilities.add(new FacilityHomepageDTO(
                    facility.getAddress(),
                    new ArrayList<>(schedules.values()),
                    facility.getPersonInCharge(),
                    facility.getPhoneNumber(),
                    facility.getMapsLink(),
                    facility.getImage()
            ));
        }
        return displayedFacilities;
    }

    public List<FacilityManagementDTO> getAllFacilitiesForManagement(User user) {
        if(user.getRole() == 1) {
            return facilityRepository.findAllByManager_Id(user.getId())
                    .stream()
                    .map(this::toFacilityManagementDTO)
                    .collect(Collectors.toList());
        }
        else if(user.getRole() == 0) {
            return facilityRepository.findAll()
                    .stream()
                    .map(this::toFacilityManagementDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }

    // --- Mapper ---
    private FacilityResponseDTO toResponseDTO(Facility facility) {
        FacilityResponseDTO dto = new FacilityResponseDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setAddress(facility.getAddress());
        dto.setPhoneNumber(facility.getPhoneNumber());
        dto.setClasses(facility.getFacilityClasses());
        dto.setImage(facility.getImage());
        dto.setMapsLink(facility.getMapsLink());

        if (facility.getManager() != null) {
            dto.setManagerUserId(facility.getManager().getId());
            dto.setManagerName(facility.getManager().getName());
        }
        return dto;
    }

    // --- Mapper ---
    private FacilityWebsiteManagementDTO toFacilityWebsiteManagementDTO(Facility facility) {
        FacilityWebsiteManagementDTO dto = new FacilityWebsiteManagementDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setAddress(facility.getAddress());
        dto.setPersonInCharge(facility.getManager() != null ? facility.getManager().getName() : "");
        dto.setPhoneNumber(facility.getPhoneNumber());
        List<FacilityClassUpdateDTO> classes = new ArrayList<>();
        for(FacilityClass facilityClass : facility.getFacilityClasses()) {
            // Map facility class to update dto
            if(!facilityClass.getIsActive()) {
                continue;
            }
            FacilityClassUpdateDTO facilityClassUpdateDTO = new FacilityClassUpdateDTO();
            facilityClassUpdateDTO.setId(facilityClass.getId());
            facilityClassUpdateDTO.setName(facilityClass.getName());
            facilityClassUpdateDTO.setDescription(facilityClass.getDescription());
            facilityClassUpdateDTO.setEndHour(facilityClass.getEndHour());
            facilityClassUpdateDTO.setStartHour(facilityClass.getStartHour());
            facilityClassUpdateDTO.setDescription(facilityClass.getDescription());
            facilityClassUpdateDTO.setIsActive(facilityClass.getIsActive());
            facilityClassUpdateDTO.setDaysOfWeek(facilityClass.getDaysOfWeek());

            classes.add(facilityClassUpdateDTO);
        }
        dto.setClasses(classes);
        dto.setImage(facility.getImage());
        dto.setMapsLink(facility.getMapsLink());

        return dto;
    }

    // --- Mapper ---
    private FacilityManagementDTO toFacilityManagementDTO(Facility facility) {
        FacilityManagementDTO dto = new FacilityManagementDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setAddress(facility.getAddress());
        dto.setPhoneNumber(facility.getPhoneNumber());
        if(facility.getManager() != null) {
            dto.setManagerId(facility.getManager().getId());
            dto.setManagerName(facility.getManager().getName());
        }
        else {
            dto.setManagerId(null);
            dto.setManagerName("");
        }
        dto.setImage(facility.getImage());
        dto.setMapsLink(facility.getMapsLink());
        dto.setIsActive(facility.getIsActive());
        dto.setCreatedAt(facility.getCreatedAt());
        dto.setUpdatedAt(facility.getUpdatedAt());
        dto.setLatitude(facility.getLatitude());
        dto.setLongitude(facility.getLongitude());
        dto.setDescription(facility.getDescription());
        List<FacilityClassGeneralInfo> classes = new ArrayList<>();
        if(facility.getFacilityClasses() != null) {
            for(FacilityClass facilityClass : facility.getFacilityClasses()) {
                // Map facility class to update dto
                FacilityClassGeneralInfo facilityClassGeneralInfo = new FacilityClassGeneralInfo();
                facilityClassGeneralInfo.setId(facilityClass.getId());
                facilityClassGeneralInfo.setName(facilityClass.getName());
                facilityClassGeneralInfo.setDescription(facilityClass.getDescription());
                facilityClassGeneralInfo.setEndHour(facilityClass.getEndHour());
                facilityClassGeneralInfo.setStartHour(facilityClass.getStartHour());
                facilityClassGeneralInfo.setDescription(facilityClass.getDescription());
                facilityClassGeneralInfo.setIsActive(facilityClass.getIsActive());
                facilityClassGeneralInfo.setDaysOfWeek(facilityClass.getDaysOfWeek());
                facilityClassGeneralInfo.setLatestSession(facilityClass.getLatestSession());
                facilityClassGeneralInfo.setSessionsUpdatedAt(facilityClass.getSessionsUpdatedAt());
                facilityClassGeneralInfo.setStudentCount(facilityClassUserRepository.countByFacilityClass_Id(facilityClass.getId()));
                classes.add(facilityClassGeneralInfo);
            }
        }
        dto.setClasses(classes);


        return dto;
    }
}
