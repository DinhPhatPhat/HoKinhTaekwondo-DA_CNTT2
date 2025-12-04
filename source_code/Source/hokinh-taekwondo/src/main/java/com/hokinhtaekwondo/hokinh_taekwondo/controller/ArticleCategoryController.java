package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.model.ArticleCategory;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/article-category")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @GetMapping("/admin/all-article-categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<ArticleCategory> categories = articleCategoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi khi truy xuất danh mục" + e.getMessage());
        }
    }

    @PostMapping("/admin/add")
    public ResponseEntity<?> createCategory(@RequestBody ArticleCategory category) {
        try {
            ArticleCategory newCategory = articleCategoryService.createCategory(category);
            return ResponseEntity.ok(newCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi khi tạo danh mục: " + e.getMessage());
        }
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody ArticleCategory category) {
        try {
            ArticleCategory updatedCategory = articleCategoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi khi cập nhật danh mục: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            articleCategoryService.deleteCategory(id);
            return ResponseEntity.ok("Đã xóa danh mục " + id);
        }
        catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Không thể xóa danh mục đã chứa bài viết.");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi khi xóa danh mục: " + e.getMessage());
        }
    }
}
