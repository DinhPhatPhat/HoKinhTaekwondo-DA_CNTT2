package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Session;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByFacilityClass_IdAndDateBetween(Integer id, LocalDate startDate, LocalDate endDate);

    Session findTopByFacilityClassOrderByDateDesc(FacilityClass facilityClass);

    @Query("SELECT s FROM Session s WHERE s.facilityClass.id = :classId " +
            "AND s.date BETWEEN :start AND :end ORDER BY s.date ASC")
    List<Session> findSessionsInRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("classId") Integer classId);

    Session findByFacilityClassAndDateAndStartTimeAndEndTime(FacilityClass facilityClass, LocalDate current, @NotNull(message = "Giờ bắt đầu không được để trống") LocalTime startTime, @NotNull(message = "Giờ kết thúc không được để trống") LocalTime endTime);
    @Query("""
    SELECT s.id FROM Session s
    WHERE s.facilityClass.id = :classId
      AND s.date BETWEEN :startDate AND :endDate
""")
    List<Integer> findIdsByFacilityClassIdAndDateBetween(
            @Param("classId") Integer classId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    void deleteAllByIdIn(List<Integer> ids);


}
