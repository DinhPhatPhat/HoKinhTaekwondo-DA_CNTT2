package com.hokinhtaekwondo.hokinh_taekwondo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Session belongs to one FacilityClass (one-to-many)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_class_id")
    private FacilityClass facilityClass;

    @Column(nullable = false)
    private LocalDate date; // The date of this session

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Start hour of the session

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // End hour of the session

    @Column(length = 255)
    private String topic; // Topic or focus of the session

    @Column(length = 500, nullable = true)
    private String report; // Summary or report for the session (initially null)

    @Column
    private int status = 0; // 0: planned | 1: in_progress | 2: done | 3: canceled

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
