package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    // --- Create ---
    public FacilityResponseDTO createFacility(FacilityRequestDTO dto) {
        Facility facility = new Facility();
        facility.setName(dto.getName());
        facility.setAddress(dto.getAddress());
        facility.setPhone(dto.getPhone());
        facility.setNote(dto.getNote());

        // Gắn manager nếu có
        if (dto.getManagerUserId() != null) {
            Optional<User> managerOpt = userRepository.findById(dto.getManagerUserId());
            managerOpt.ifPresent(facility::setManager);
        }

        Facility saved = facilityRepository.save(facility);
        return toResponseDTO(saved);
    }

    // --- Update ---
    public FacilityResponseDTO updateFacility(Integer id, FacilityRequestDTO dto) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        facility.setName(dto.getName());
        facility.setAddress(dto.getAddress());
        facility.setPhone(dto.getPhone());
        facility.setNote(dto.getNote());

        if (dto.getManagerUserId() != null) {
            Optional<User> managerOpt = userRepository.findById(dto.getManagerUserId());
            managerOpt.ifPresent(facility::setManager);
        } else {
            facility.setManager(null);
        }

        Facility updated = facilityRepository.save(facility);
        return toResponseDTO(updated);
    }

    // --- Delete ---
    public void deleteFacility(Integer id) {
        if (!facilityRepository.existsById(id)) {
            throw new RuntimeException("Facility not found");
        }
        facilityRepository.deleteById(id);
    }

    // --- Get by ID ---
    public FacilityResponseDTO getFacilityResponseDTOById(Integer id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        return toResponseDTO(facility);
    }

    public Facility getFacilityById(Integer id){
        return facilityRepository.findById(id).orElse(null);
    }

    // --- Get all ---
    public List<FacilityResponseDTO> getAllFacilities() {
        return facilityRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Mapper ---
    private FacilityResponseDTO toResponseDTO(Facility facility) {
        FacilityResponseDTO dto = new FacilityResponseDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setAddress(facility.getAddress());
        dto.setPhone(facility.getPhone());
        dto.setNote(facility.getNote());

        if (facility.getManager() != null) {
            dto.setManagerUserId(facility.getManager().getId());
            dto.setManagerName(facility.getManager().getName());
        }
        return dto;
    }
}
