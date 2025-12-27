package com.hokinhtaekwondo.hokinh_taekwondo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BotFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String name;
    @Column
    private String size;
    @Column
    private String uploadDate;
    @Column
    private String status; // indexed, processing, error
    @Column
    private String type;
    @Column
    private String pythonFileId;
}
