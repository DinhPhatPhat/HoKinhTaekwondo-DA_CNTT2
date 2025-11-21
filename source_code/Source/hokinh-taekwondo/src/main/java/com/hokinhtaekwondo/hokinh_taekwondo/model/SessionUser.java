package com.hokinhtaekwondo.hokinh_taekwondo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_users")
@Getter
@Setter
public class SessionUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ID of the session
    @Column(name = "session_id", nullable = false, length = 100)
    private int sessionId;

    // ID of the user
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    // Role of this user in the session (leader, assistant, student)
    @Column(name = "role_in_session", length = 50)
    private String roleInSession;

    // Review given by the user for this session
    @Column
    private String review;

    // Attendance status
    @Column
    private Boolean attended = false;

    // Checkin time by coach or instructor
    @Column(name = "checkin_time")
    private LocalDateTime checkinTime = null; // When the user checked in
}
