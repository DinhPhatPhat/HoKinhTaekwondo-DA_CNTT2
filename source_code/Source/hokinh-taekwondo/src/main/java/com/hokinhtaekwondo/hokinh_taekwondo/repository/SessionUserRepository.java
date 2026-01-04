package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.SessionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SessionUserRepository extends JpaRepository<SessionUser, Integer> {
    List<SessionUser> findBySessionId(Integer sessionId);
    List<SessionUser> findBySessionIdAndRoleInSessionEquals(Integer sessionId, String roleInSession);
    void  deleteAllBySessionId(Integer sessionId);
    void deleteAllBySessionIdIn(List<Integer> sessionIds);
    SessionUser findBySessionIdAndUserId(Integer sessionId, String userId);
    void deleteAllByUserId(String userId);
}
