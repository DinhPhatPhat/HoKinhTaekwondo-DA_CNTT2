package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.article.ArticleDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Article;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ArticleService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @GetMapping("/homepage")
    public ResponseEntity<?> getArticleHomepage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size
    ) {
        return ResponseEntity.ok(articleService.getArticlesHomepage(page, size));
    }

    @GetMapping("/all-articles-by-category")
    public ResponseEntity<?> getAllArticlesByCategory(@RequestParam Integer categoryId) {
        try {
            List<ArticleDTO> articles = articleService.getAllArticlesByCategory(categoryId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching articles by category: " + e.getMessage());
        }
    }

    @GetMapping("/all-deleted-articles")
    public ResponseEntity<?> getAllDeletedArticles() {
        try {
            List<ArticleDTO> deletedArticles = articleService.getAllDeletedArticles();
            return ResponseEntity.ok(deletedArticles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching deleted articles: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> createArticle(@RequestBody ArticleDTO article) {
        try {
            ArticleDTO savedArticle = articleService.createArticle(article);
            return ResponseEntity.ok(savedArticle);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating article: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Integer id, @RequestBody ArticleDTO article) {
        try {
            ArticleDTO updatedArticle = articleService.updateArticle(id, article);
            return ResponseEntity.ok(updatedArticle);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error updating article: " + e.getMessage());
        }
    }

    @PutMapping("/patch/{id}")
    public ResponseEntity<?> patchArticle(@PathVariable Integer id, @RequestBody ArticleDTO article) {
        try {
            ArticleDTO patchedArticle = articleService.patchArticle(id, article);
            return ResponseEntity.ok(patchedArticle);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error updating article: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Integer id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok("Xóa thành công tin tức với id là: " + id);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}
