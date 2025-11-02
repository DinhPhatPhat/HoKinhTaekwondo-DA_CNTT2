package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "facility_class_users")
@Getter
@Setter
public class FacilityClassUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_class_id", nullable = false)
    @JsonBackReference
    private FacilityClass facilityClass;

    // User ID
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    // Role of user in class (coach, instructor, student, etc.)
    @Column(name = "role_in_facility_class", nullable = false, length = 50)
    private String roleInClass;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
