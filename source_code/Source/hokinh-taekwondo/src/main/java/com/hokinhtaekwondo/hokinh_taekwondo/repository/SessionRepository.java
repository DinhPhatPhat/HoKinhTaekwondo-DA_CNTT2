package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.InstructorSessionInfo;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.SessionAttendanceDTO;
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

    @Query("""
        SELECT s, su
        FROM Session s
        JOIN FETCH s.facilityClass fc
        JOIN FETCH fc.facility fac
        JOIN SessionUser su ON su.sessionId = s.id
        WHERE su.userId = :userId
          AND s.date BETWEEN :startDate AND :endDate
    """)
    List<InstructorSessionInfo> findAllSessionsForInstructorByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT new com.hokinhtaekwondo.hokinh_taekwondo.dto.session.SessionAttendanceDTO(
            su.userId,
            su.roleInSession,
            u.name,
            s.date,
            s.startTime,
            su.checkinTime,
            su.attended,
            f.id,
            fc.id
        )
        FROM Session s
        JOIN s.facilityClass fc
        JOIN fc.facility f
        JOIN SessionUser su ON su.sessionId = s.id
        JOIN User u ON u.id = su.userId
        WHERE s.status = 0 AND u.isActive = true AND f.id = :facilityId
          AND s.date BETWEEN :startDate AND :endDate
    """)
    List<SessionAttendanceDTO> findAllSessionsForInstructorsByFacilityId(
            @Param("facilityId") Integer facilityId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
