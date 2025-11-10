package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Session;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByFacilityClass_IdAndDateBetween(Integer id, LocalDate startDate, LocalDate endDate);

    boolean existsByFacilityClassAndDateAndStartTimeAndEndTime(FacilityClass facilityClass, LocalDate current, @NotNull(message = "Giờ bắt đầu không được để trống") LocalTime startTime, @NotNull(message = "Giờ kết thúc không được để trống") LocalTime endTime);
}
