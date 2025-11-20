package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.SessionUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionUserRepository extends JpaRepository<SessionUser, Integer> {
    SessionUser findBySessionIdAndUserId(Integer sessionId, String userId);
}
