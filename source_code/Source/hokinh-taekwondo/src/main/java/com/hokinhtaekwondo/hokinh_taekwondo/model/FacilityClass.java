package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "facility_classes")
@Getter
@Setter
public class FacilityClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    @JsonBackReference
    private Facility facility;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Các ngày học (ví dụ "2,4,6")
    @Column(name = "days_of_week", length = 20)
    private String daysOfWeek;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "facilityClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FacilityClassUser> facilityClassUsers;

    @OneToMany(mappedBy = "facilityClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Session> sessions;
}
