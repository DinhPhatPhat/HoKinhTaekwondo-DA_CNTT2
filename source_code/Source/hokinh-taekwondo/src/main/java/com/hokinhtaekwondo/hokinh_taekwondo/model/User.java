package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 255)
    private String avatar;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    // Role ( 0: club_head, 1: manager, 2: coach, 3: instructor, 4: student, etc.)
    @Column(nullable = false, length = 50)
    private Integer role;

    // Belt level (plain text)
    @Column(name = "belt_level", length = 100)
    private String beltLevel = "Không";

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Many users belong to one facility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    @JsonBackReference
    private Facility facility;
}
