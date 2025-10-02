package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "facilities")
@Getter
@Setter
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200, unique = true)
    private String name;

    @Column(length = 400)
    private String address;

    @Column(length = 10)
    private String phone;

    @Column(length = 255)
    private String note;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    // Manager của cơ sở (khóa ngoại đến users.id)
    @ManyToOne
    @JoinColumn(name = "manager_user_id", referencedColumnName = "id")
    private User manager;

    // Danh sách user thuộc cơ sở này
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<User> users;
}
