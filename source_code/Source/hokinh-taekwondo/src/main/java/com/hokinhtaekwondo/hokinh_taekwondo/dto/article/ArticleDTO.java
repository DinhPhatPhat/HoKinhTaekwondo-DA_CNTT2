package com.hokinhtaekwondo.hokinh_taekwondo.dto.article;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hokinhtaekwondo.hokinh_taekwondo.model.ArticleCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleDTO {

    private Integer id;

    private String title;

    private String content;

    private String coverImage;

    private LocalDateTime date;

    private String author;

    private List<String> gallery;

    private ArticleCategory category;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    private String type;
}
