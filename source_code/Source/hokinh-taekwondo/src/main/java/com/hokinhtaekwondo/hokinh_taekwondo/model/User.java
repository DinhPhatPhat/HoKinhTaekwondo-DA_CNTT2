package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @Column(length = 100)
    private String id;   // <-- this will become username (unique)

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 255)
    private String avatar;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    // Role (0: club_head, 1: manager, 2: coach, 3: instructor, 4: student)
    @Column(nullable = false, length = 50)
    private Integer role;

    @Column(name = "belt_level", length = 100)
    private String beltLevel = "Không";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    @JsonBackReference
    private Facility facility;

    @Column
    private Integer loginPin;


    // ================================================================
    // UserDetails implementations
    // ================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRoleName(role)));
    }

    private String getRoleName(Integer role) {
        return switch (role) {
            case 0 -> "CLUB_HEAD";
            case 1 -> "MANAGER";
            case 2 -> "COACH";
            case 3 -> "INSTRUCTOR";
            case 4 -> "STUDENT";
            default -> throw new IllegalArgumentException("Invalid role value: " + role);
        };
    }

    @Override
    public String getUsername() {
        return id;   // <-- USE ID AS USERNAME
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
