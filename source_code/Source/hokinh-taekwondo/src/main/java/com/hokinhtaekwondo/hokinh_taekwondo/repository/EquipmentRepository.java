package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Equipment;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    List<Equipment> findAllByFacility_Manager_Id(String facilityManagerId);
    @Query("""
    SELECT DISTINCT eq.facility.manager.id
    FROM Equipment eq
    WHERE eq.facility.manager.id != null
    """)
    List<String> findManagersOfEquipments(List<Integer> equipmentIds);
}
