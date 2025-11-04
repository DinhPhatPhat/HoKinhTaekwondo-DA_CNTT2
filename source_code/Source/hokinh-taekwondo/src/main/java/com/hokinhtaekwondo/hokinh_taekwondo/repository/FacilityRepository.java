package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Integer> {
    List<Facility> findAllByManager_Id(String managerId);

    List<Facility> findAllByIsActive(Boolean isActive);
}
