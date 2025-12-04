package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClassUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacilityClassUserRepository extends JpaRepository<FacilityClassUser, Integer> {
    List<FacilityClassUser> findByFacilityClass_Id(Integer facilityClassId);

    Optional<FacilityClassUser> findByFacilityClassIdAndUserId(Integer facilityClassId, String userId);

    List<FacilityClassUser> findByFacilityClass_IdAndIsActiveTrue(Integer facilityClassId);

    List<FacilityClassUser> findByFacilityClass_IdAndIsActiveFalse(Integer facilityClassId);

    Optional<FacilityClassUser> findByUserIdAndIsActiveTrue(String userId);

    @Query("SELECT fcu.facilityClass.id FROM FacilityClassUser fcu " +
            "WHERE fcu.userId = :userId AND fcu.isActive = true")
    Integer findActiveClassForUser(@Param("userId") String userId);

    void deleteAllByFacilityClass_IdAndUserIdIn(Integer facilityClassId,  List<String> userIds);
}
