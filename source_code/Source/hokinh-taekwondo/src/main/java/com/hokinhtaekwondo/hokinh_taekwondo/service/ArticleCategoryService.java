package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.model.ArticleCategory;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.ArticleCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCategoryService {

    private final ArticleCategoryRepository articleCategoryRepository;

    public ArticleCategoryService(ArticleCategoryRepository articleCategoryRepository) {
        this.articleCategoryRepository = articleCategoryRepository;
    }

    public List<ArticleCategory> getAllCategories() {
        return articleCategoryRepository.findAll();
    }

    public ArticleCategory createCategory(ArticleCategory category) {
        return articleCategoryRepository.save(category);
    }

    public ArticleCategory updateCategory(Integer id, ArticleCategory updatedCategory) {
        ArticleCategory category = articleCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có id là: " + id));

        category.setCategoryName(updatedCategory.getCategoryName());
        return articleCategoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        if (!articleCategoryRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy danh mục có id là: " + id);
        }
        articleCategoryRepository.deleteById(id);
    }
}
