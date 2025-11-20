package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.sessionUser.CheckinRequestDTO;
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
}
