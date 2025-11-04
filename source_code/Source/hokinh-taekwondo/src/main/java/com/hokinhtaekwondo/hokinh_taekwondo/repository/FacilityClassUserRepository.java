package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClassUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityClassUserRepository extends JpaRepository<FacilityClassUser, Integer> {
    List<FacilityClassUser> findByFacilityClass_Id(Integer facilityClassId);

    List<FacilityClassUser> findByFacilityClass_IdAndIsActiveTrue(Integer facilityClassId);

    List<FacilityClassUser> findByFacilityClass_IdAndIsActiveFalse(Integer facilityClassId);
}
