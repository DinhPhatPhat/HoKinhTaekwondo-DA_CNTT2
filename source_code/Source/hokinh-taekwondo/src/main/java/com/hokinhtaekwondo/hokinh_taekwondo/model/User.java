package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 255)
    private String avatar;

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "belt_level", length = 50)
    private BeltLevel beltLevel = BeltLevel.KHONG;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Quan hệ nhiều User thuộc về 1 Facility
    @ManyToOne
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    @JsonBackReference
    private Facility facility;

    @Getter
    public enum Role {
        manager,
        club_head,
        coach,
        student
    }

    @Getter
    public enum BeltLevel {
        KHONG("Không"),
        CAP_8_TRANG("Cấp 8 - Trắng"),
        CAP_7_VANG("Cấp 7 - Vàng"),
        CAP_6_XANH_LA_CAY("Cấp 6 - Xanh lá cây"),
        CAP_5_XANH_DUONG("Cấp 5 - Xanh dương"),
        CAP_4_DO("Cấp 4 - Đỏ"),
        CAP_3_DO("Cấp 3 - Đỏ"),
        CAP_2_DO("Cấp 2 - Đỏ"),
        CAP_1_DO("Cấp 1 - Đỏ"),
        MOT_DANG("Một đẳng - Đen 1 vạch vàng"),
        HAI_DANG("Hai đẳng - Đen 2 vạch vàng"),
        BA_DANG("Ba đẳng - Đen 3 vạch vàng"),
        BON_DANG("Bốn đẳng - Đen 4 vạch vàng"),
        NAM_DANG("Năm đẳng - Đen 5 vạch vàng"),
        SAU_DANG("Sáu đẳng - Đen 6 vạch đỏ"),
        BAY_DANG("Bảy đẳng - Đen 7 vạch đỏ"),
        TAM_DANG("Tám đẳng - Đen 8 vạch trắng"),
        CHIN_DANG("Chín đẳng - Đen 9 vạch trắng"),
        MUOI_DANG("Mười đẳng - Đen 10 vạch trắng");

        BeltLevel(String s) {
        }
    }

}
