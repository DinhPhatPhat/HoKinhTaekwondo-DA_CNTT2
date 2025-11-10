package com.hokinhtaekwondo.hokinh_taekwondo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "author")
    private String author;

    @Column(name = "gallery", columnDefinition = "TEXT")
    private String gallery;

    @JoinColumn(name = "category_id")
    @ManyToOne
    @JsonManagedReference
    private ArticleCategory category;

    @Column
    private boolean isDeleted;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private String type;
}
