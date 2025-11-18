package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.SessionBulkUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.SessionAndSessionUserBulkCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.SessionCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.SessionUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.SessionUserDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Session;
import com.hokinhtaekwondo.hokinh_taekwondo.model.SessionUser;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.SessionRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.SessionUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final FacilityClassRepository facilityClassRepository;
    private final SessionUserRepository sessionUserRepository;

    // ================= BULK CREATE ==================
    @Transactional
    public void bulkCreateSessions(List<SessionCreateDTO> sessionList) {
        for (SessionCreateDTO dto : sessionList) {
            FacilityClass facilityClass = facilityClassRepository.findById(dto.getFacilityClassId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp có ID: " + dto.getFacilityClassId()));

            Session newSession = new Session();
            newSession.setFacilityClass(facilityClass);
            newSession.setDate(dto.getDate());
            newSession.setStartTime(dto.getStartTime());
            newSession.setEndTime(dto.getEndTime());
            newSession.setTopic(dto.getTopic());
            newSession.setVideoLink(dto.getVideoLink());
            newSession.setReport(dto.getReport());
            newSession.setStatus(dto.getStatus());

            sessionRepository.save(newSession);
        }
    }

    @Transactional
    public int bulkCreateSessionsAndUsers(Integer facilityClassId,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          List<SessionAndSessionUserBulkCreateDTO> dtoList) {

        FacilityClass facilityClass = facilityClassRepository.findById(facilityClassId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học."));

        int createdCount = 0;

        for (SessionAndSessionUserBulkCreateDTO dto : dtoList) {
            LocalDate current = startDate;

            while (!current.isAfter(endDate)) {
                // Kiểm tra xem có phải ngày trong tuần của DTO
                if (current.getDayOfWeek().getValue() == dto.getDayOfWeek()) {

                    // Kiểm tra trùng session
                    boolean exists = sessionRepository.existsByFacilityClassAndDateAndStartTimeAndEndTime(
                            facilityClass, current, dto.getStartTime(), dto.getEndTime());

                    if (!exists) {
                        // Tạo session
                        Session s = new Session();
                        s.setFacilityClass(facilityClass);
                        s.setDate(current);
                        s.setStartTime(dto.getStartTime());
                        s.setEndTime(dto.getEndTime());
                        sessionRepository.save(s);

                        // Tạo sessionUser
                        for (SessionUserDTO u : dto.getUsers()) {
                            SessionUser su = new SessionUser();
                            su.setSessionId(s.getId());
                            su.setUserId(u.getId());
                            su.setRoleInSession(u.getRoleInSession());
                            sessionUserRepository.save(su);
                        }
                        createdCount++;
                    }
                }
                current = current.plusDays(1); // How about +7
            }
        }
        // Save the latest update time
        facilityClass.setSessionsUpdatedAt(LocalDateTime.now());
        facilityClassRepository.save(facilityClass);
        return createdCount;
    }

    // ================= BULK UPDATE ==================
    @Transactional
    public void bulkUpdateSessions(SessionBulkUpdateDTO sessionBulkUpdateDTO) {
        if (sessionBulkUpdateDTO.getFacilityClassId() == null || facilityClassRepository.findById(sessionBulkUpdateDTO.getFacilityClassId()).isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy lớp học");
        }
        for (SessionUpdateDTO dto : sessionBulkUpdateDTO.getSessions()) {
            Session existingSession = sessionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy buổi học có ID: " + dto.getId()));

            // Chỉ cập nhật các trường cho phép
            if (dto.getDate() != null) existingSession.setDate(dto.getDate());
            if (dto.getStartTime() != null) existingSession.setStartTime(dto.getStartTime());
            if (dto.getEndTime() != null) existingSession.setEndTime(dto.getEndTime());
            if (dto.getTopic() != null) existingSession.setTopic(dto.getTopic());
            if (dto.getVideoLink() != null) existingSession.setVideoLink(dto.getVideoLink());
            if (dto.getReport() != null) existingSession.setReport(dto.getReport());
            if (dto.getStatus() != null) existingSession.setStatus(dto.getStatus());

            sessionRepository.save(existingSession);
        }
    }

    public List<Session> getSessionsByFacilityClassAndDateRange(Integer facilityClassId, LocalDate startDate, LocalDate endDate) {
        FacilityClass facilityClass = facilityClassRepository.findById(facilityClassId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học có ID: " + facilityClassId));

        return sessionRepository.findByFacilityClass_IdAndDateBetween(facilityClass.getId(), startDate, endDate);
    }

}
