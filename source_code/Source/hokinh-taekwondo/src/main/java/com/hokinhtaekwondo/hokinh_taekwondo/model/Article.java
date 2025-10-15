package com.hokinhtaekwondo.hokinh_taekwondo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column
    private String coverImage;
    @Column
    private LocalDateTime date;
    @Column
    private String category;
    @Column
    private String author;
    @Column(columnDefinition = "TEXT")
    private String gallery;

    public Article(String title, String content, String coverImage, LocalDateTime date, String category,  String author, String gallery) {
        this.title = title;
        this.content = content;
        this.coverImage = coverImage;
        this.date = date;
        this.category = category;
        this.author = author;
        this.gallery = gallery;
    }
}
