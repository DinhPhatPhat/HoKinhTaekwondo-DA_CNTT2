package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.CheckinRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.StudentAttendanceDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.StudentReviewDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Session;
import com.hokinhtaekwondo.hokinh_taekwondo.model.SessionUser;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.SessionRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.SessionUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionUserService {

    private final SessionUserRepository sessionUserRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public String checkin(String userId, CheckinRequestDTO dto) {

        SessionUser sessionUser = sessionUserRepository
                .findBySessionIdAndUserId(dto.getSessionId(), userId);
        if(sessionUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng trong buổi học này");
        }

        Session sessionEntity = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy buổi học."));

        FacilityClass facilityClass = sessionEntity.getFacilityClass();
        Facility facility = facilityClass.getFacility();

        double distance = haversine(dto.getLatitude(), dto.getLongitude(),
                facility.getLatitude(), facility.getLongitude());

        if (distance > 50) {
            throw new SecurityException("Bạn đang ở quá xa để check-in.");
        }

        sessionUser.setAttended(true);
        sessionUser.setCheckinTime(LocalDateTime.now());
        sessionUserRepository.save(sessionUser);

        return "Check-in thành công tại khoảng cách " + Math.round(distance) + "m.";
    }

    private double haversine(double lat1, double lon1, BigDecimal lat2, BigDecimal lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2.doubleValue() - lat1);
        double dLon = Math.toRadians(lon2.doubleValue() - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public void markAttendance(String currentUserId, StudentAttendanceDTO dto) throws Exception {

        // 1. Kiểm tra current user có trong session chưa
        SessionUser currentRole = sessionUserRepository
                .findBySessionIdAndUserId(dto.getSessionId(), currentUserId);
        if(currentRole == null) {
            throw new Exception("Bạn không thuộc buổi học này!");
        }

        // 2. Kiểm tra quyền
        if (!currentRole.getRoleInSession().equals("leader") &&
                !currentRole.getRoleInSession().equals("assistant")) {
            throw new Exception("Bạn không có quyền điểm danh!");
        }

        // 3. Lấy student trong session
        SessionUser student = sessionUserRepository
                .findBySessionIdAndUserId(dto.getSessionId(), dto.getStudentId());
        if(student == null) {
            throw new Exception("Không thấy học sinh này trong buổi học!");
        }

        // 4. Cập nhật attendance
        student.setAttended(dto.getAttended());
        sessionUserRepository.save(student);
    }


    public void markReview(String currentUserId, StudentReviewDTO dto) throws Exception {

        // 1. Kiểm tra current user có trong session không
        SessionUser currentRole = sessionUserRepository
                .findBySessionIdAndUserId(dto.getSessionId(), currentUserId);

        if (currentRole == null) {
            throw new Exception("Bạn không thuộc buổi học này!");
        }

        // 2. Kiểm tra quyền: chỉ leader hoặc assistant được đánh giá
        if (!currentRole.getRoleInSession().equals("leader") &&
                !currentRole.getRoleInSession().equals("assistant")) {
            throw new Exception("Bạn không có quyền đánh giá học sinh!");
        }

        // 3. Lấy học sinh trong session
        SessionUser student = sessionUserRepository
                .findBySessionIdAndUserId(dto.getSessionId(), dto.getStudentId());

        if (student == null) {
            throw new Exception("Không thấy học sinh này trong buổi học!");
        }

        // 4. Cập nhật review
        student.setReview(dto.getReview());
        sessionUserRepository.save(student);
    }

}
