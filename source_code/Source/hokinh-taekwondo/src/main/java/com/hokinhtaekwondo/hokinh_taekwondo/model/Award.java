package com.hokinhtaekwondo.hokinh_taekwondo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column(name = "award_rank")
    private String rank;
    @Column
    private String year;
    @Column
    private String description;
    @Column
    private String img;

    public Award(String name, String rank, String year, String description, String img) {
        this.name = name;
        this.rank = rank;
        this.year = year;
        this.description = description;
        this.img = img;
    }
}
