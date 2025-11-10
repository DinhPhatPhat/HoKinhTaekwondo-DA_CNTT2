package com.hokinhtaekwondo.hokinh_taekwondo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @Column(name = "award_rank")
    private String rank;
    @Column
    private String year;
    @Column
    private String description;
    @Column
    private String image;
    @Column
    private boolean isDeleted;
    @Column
    private LocalDateTime deletedAt;

    public Award(String name, String rank, String year, String description, String image) {
        this.name = name;
        this.rank = rank;
        this.year = year;
        this.description = description;
        this.image = image;
    }
}
