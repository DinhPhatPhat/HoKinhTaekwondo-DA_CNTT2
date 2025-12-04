package com.hokinhtaekwondo.hokinh_taekwondo.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Belongs to one facility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "unit", length = 30, nullable = false)
    private String unit;

    @Column
    private Integer damagedQuantity;

    @Column
    private Integer goodQuantity;

    @Column
    private Integer fixableQuantity;

    @Column(columnDefinition = "TEXT")
    private String damagedDescription;

    @Column(columnDefinition = "TEXT")
    private String goodDescription;

    @Column(columnDefinition = "TEXT")
    private String fixableDescription;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
