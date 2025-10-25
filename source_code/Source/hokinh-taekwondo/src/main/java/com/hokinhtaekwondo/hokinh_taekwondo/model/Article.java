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
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "category")
    private String category;

    @Column(name = "author")
    private String author;

    @Column(name = "gallery", columnDefinition = "TEXT")
    private String gallery;

    @Column(name = "is_active")
    private String isActive;

    public Article(String title, String content, String coverImage, LocalDateTime date,
                   String category, String author, String gallery) {
        this.title = title;
        this.content = content;
        this.coverImage = coverImage;
        this.date = date;
        this.category = category;
        this.author = author;
        this.gallery = gallery;
    }
}
