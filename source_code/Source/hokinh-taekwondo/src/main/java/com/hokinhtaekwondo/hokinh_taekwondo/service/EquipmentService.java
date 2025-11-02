package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.equipment.EquipmentUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Equipment;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.EquipmentRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityService facilityService;

    // --- Create ---
    public void createEquipment(EquipmentCreateDTO dto) {
        Equipment equipment = new Equipment();
        equipment.setName(dto.getName());
        equipment.setDescription(dto.getDescription());
        equipment.setStatus(dto.getStatus());

        Facility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));
        equipment.setFacility(facility);

        equipmentRepository.save(equipment);
    }

    // --- Update ---
    public void updateEquipment(Integer id, EquipmentUpdateDTO dto) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị có ID = " + id));

        if (StringUtils.hasText(dto.getName())) {
            equipment.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            equipment.setDescription(dto.getDescription());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            equipment.setStatus(dto.getStatus());
        }
        if (dto.getFacilityId() != null) {
            Facility facility = facilityRepository.findById(dto.getFacilityId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));
            equipment.setFacility(facility);
        }

        equipmentRepository.save(equipment);
    }

    // --- Delete ---
    public void deleteEquipment(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị có ID = " + id));

        equipmentRepository.delete(equipment);
    }

    // --- Find All ---
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> getEquipmentsByManagerId(String managerId) {
        return equipmentRepository.findAllByFacility_Manager_Id(managerId);
    }

    public Equipment getById(Integer id) {
        return equipmentRepository.findById(id).orElse(null);
    }
}
