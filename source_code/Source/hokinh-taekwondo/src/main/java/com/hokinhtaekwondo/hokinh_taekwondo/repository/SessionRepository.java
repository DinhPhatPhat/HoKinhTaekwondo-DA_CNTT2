package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByFacilityClass_IdAndDateBetween(Integer id, LocalDate startDate, LocalDate endDate);
}
