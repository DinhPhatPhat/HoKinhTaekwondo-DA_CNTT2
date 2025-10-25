package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facility.FacilityUpdateDTO;
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
    public void createFacility(FacilityRequestDTO dto) {
        Facility facility = new Facility();
        facility.setName(dto.getName());
        facility.setAddress(dto.getAddress());
        facility.setPhoneNumber(dto.getPhoneNumber());
        facility.setDescription(dto.getDescription());

        // Gắn manager nếu có
        if (dto.getManagerUserId() != null) {
            Optional<User> managerOpt = userRepository.findById(dto.getManagerUserId());
            managerOpt.ifPresent(facility::setManager);
        }

        facilityRepository.save(facility);

    }

    // --- Update ---
    public void updateFacility(Integer id, FacilityUpdateDTO dto) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        facility.setName(dto.getName());
        facility.setAddress(dto.getAddress());
        facility.setPhoneNumber(dto.getPhoneNumber());
        facility.setDescription(dto.getDescription());

        if (dto.getManagerUserId() != null) {
            Optional<User> managerOpt = userRepository.findById(dto.getManagerUserId());
            managerOpt.ifPresent(facility::setManager);
        } else {
            facility.setManager(null);
        }

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

    // --- Get by ID ---
    public FacilityResponseDTO getFacilityResponseDTOById(Integer id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        return toResponseDTO(facility);
    }


    // --- Get by Id ---
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
        dto.setPhoneNumber(facility.getPhoneNumber());
        dto.setClasses(facility.getClasses());
        dto.setImage(facility.getImage());
        dto.setMapsLink(facility.getMapsLink());

        if (facility.getManager() != null) {
            dto.setManagerUserId(facility.getManager().getId());
            dto.setManagerName(facility.getManager().getName());
        }
        return dto;
    }
}
