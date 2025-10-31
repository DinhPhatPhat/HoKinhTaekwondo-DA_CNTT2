package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityClassRepository extends JpaRepository<FacilityClass, Integer> {
    List<FacilityClass> findAllByFacility_Manager_Id(String managerId);
}
