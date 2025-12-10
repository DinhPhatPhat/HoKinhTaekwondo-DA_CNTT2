package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Equipment;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.EquipmentRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityService facilityService;

    // **AUTHENTICATION
    // --- Create ---
    @Transactional
    public EquipmentDTO createEquipment(EquipmentCreateDTO dto) {
        Equipment equipment = new Equipment();
        equipment.setName(dto.getName());
        equipment.setUnit(dto.getUnit());
        equipment.setGoodDescription(dto.getGoodDescription());
        equipment.setGoodQuantity(dto.getGoodQuantity());
        equipment.setDamagedDescription(dto.getDamagedDescription());
        equipment.setDamagedQuantity(dto.getDamagedQuantity());
        equipment.setFixableDescription(dto.getFixableDescription());
        equipment.setFixableQuantity(dto.getFixableQuantity());
        equipment.setGoodQuantity(dto.getGoodQuantity());

        Facility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));
        equipment.setFacility(facility);
        equipment.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        equipment.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        return toDTO(equipmentRepository.save(equipment));
    }

    // **AUTHENTICATION
    @Transactional
    public void updateEquipments(List<EquipmentUpdateDTO> equipments) {
        for (EquipmentUpdateDTO equipmentDTO : equipments) {
            updateEquipment(equipmentDTO);
        }
    }

    // --- Update ---
    private void updateEquipment(EquipmentUpdateDTO dto) {
        Equipment equipment = equipmentRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị có ID = " + dto.getId()));

        if (StringUtils.hasText(dto.getName())) {
            equipment.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getGoodDescription())) {
            equipment.setFixableDescription(dto.getGoodDescription());
        }
        if (StringUtils.hasText(dto.getFixableDescription())) {
            equipment.setFixableDescription(dto.getFixableDescription());
        }
        if (StringUtils.hasText(dto.getDamagedDescription())) {
            equipment.setDamagedDescription(dto.getDamagedDescription());
        }
        if(dto.getGoodQuantity() != null) {
            equipment.setGoodQuantity(dto.getGoodQuantity());
        }
        if(dto.getDamagedQuantity() != null) {
            equipment.setDamagedQuantity(dto.getDamagedQuantity());
        }
        if(dto.getFixableQuantity() != null) {
            equipment.setFixableQuantity(dto.getFixableQuantity());
        }
        if (StringUtils.hasText(dto.getUnit())) {
            equipment.setUnit(dto.getUnit());
        }

        equipment.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        equipmentRepository.save(equipment);
    }

    // **AUTHENTICATION
    // --- Delete ---
    public void deleteEquipment(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị có ID = " + id));

        equipmentRepository.delete(equipment);
    }

    // --- Find All ---
    public List<EquipmentDTO> getAllEquipments() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<EquipmentDTO> getEquipmentsByManagerId(String managerId) {
        return equipmentRepository.findAllByFacility_Manager_Id(managerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Equipment getById(Integer id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    public EquipmentDTO toDTO(Equipment equipment) {
        EquipmentDTO equipmentDTO = new EquipmentDTO();
        equipmentDTO.setId(equipment.getId());
        equipmentDTO.setName(equipment.getName());
        equipmentDTO.setFacilityId(equipment.getFacility().getId());

        equipmentDTO.setUnit(equipment.getUnit());

        equipmentDTO.setDamagedDescription(equipment.getDamagedDescription());
        equipmentDTO.setDamagedQuantity(equipment.getDamagedQuantity());
        equipmentDTO.setGoodQuantity(equipment.getGoodQuantity());
        equipmentDTO.setGoodDescription(equipment.getGoodDescription());
        equipmentDTO.setFixableQuantity(equipment.getFixableQuantity());
        equipmentDTO.setFixableDescription(equipment.getFixableDescription());

        equipmentDTO.setCreatedAt(equipment.getCreatedAt());
        equipmentDTO.setUpdatedAt(equipment.getUpdatedAt());

        return equipmentDTO;
    }
}
