package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Article;
import com.hokinhtaekwondo.hokinh_taekwondo.model.ArticleCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findByCategoryAndIsDeletedFalse(ArticleCategory category, Sort sort);

    List<Article> findByIsDeletedTrue(Sort sort);

    @Query("SELECT a FROM Article a WHERE a.isDeleted = false")
    List<Article> findAllActiveArticles();
}
