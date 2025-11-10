package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.article.ArticleDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Article;
import com.hokinhtaekwondo.hokinh_taekwondo.model.ArticleCategory;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.ArticleCategoryRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository articleCategoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CloudinaryService cloudinaryService;

    // ----------- HELPER: Convert Article to DTO -----------
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setCoverImage(article.getCoverImage());
        dto.setDate(article.getDate());
        dto.setAuthor(article.getAuthor());
        dto.setCategory(article.getCategory());
        dto.setDeleted(article.isDeleted());
        dto.setDeletedAt(article.getDeletedAt());
        dto.setType(article.getType());

        // Parse JSON string to List<String>
        try {
            if (article.getGallery() != null && !article.getGallery().isEmpty()) {
                List<String> galleryList = objectMapper.readValue(
                        article.getGallery(),
                        new TypeReference<List<String>>() {}
                );
                dto.setGallery(galleryList);
            } else {
                dto.setGallery(Collections.emptyList());
            }
        } catch (Exception e) {
            dto.setGallery(Collections.emptyList());
        }

        return dto;
    }

    private Article convertToEntity(ArticleDTO dto) {
        Article article = new Article();
        article.setId(dto.getId());
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCoverImage(dto.getCoverImage());
        article.setDate(dto.getDate() != null ? dto.getDate() : LocalDateTime.now());
        article.setAuthor(dto.getAuthor());
        article.setCategory(dto.getCategory());
        article.setDeleted(dto.isDeleted());
        article.setDeletedAt(dto.getDeletedAt());
        article.setType(dto.getType());

        try {
            if (dto.getGallery() != null && !dto.getGallery().isEmpty()) {
                String galleryJson = objectMapper.writeValueAsString(dto.getGallery());
                article.setGallery(galleryJson);
            } else {
                article.setGallery("[]"); // default empty array
            }
        } catch (Exception e) {
            article.setGallery("[]");
        }

        return article;
    }


    // ----------- GET ARTICLES FOR HOMEPAGE -----------
    public Page<ArticleDTO> getArticlesHomepage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<Article> articlePage = articleRepository.findAll(pageable);
        return articlePage.map(this::convertToDTO);
    }

    // ----------- GET ALL ARTICLES BY CATEGORY -----------
    public List<ArticleDTO> getAllArticlesByCategory(Integer categoryId) {
        ArticleCategory category = articleCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có id là " + categoryId));

        List<Article> articles = articleRepository.findByCategoryAndIsDeletedFalse(
                category,
                Sort.by(Sort.Direction.DESC, "date")
        );

        return articles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ----------- GET ALL DELETED ARTICLES -----------
    public List<ArticleDTO> getAllDeletedArticles() {
        return articleRepository.findByIsDeletedTrue(Sort.by(Sort.Direction.DESC, "deletedAt"))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ----------- CREATE ARTICLE -----------
    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        articleDTO.setDate(LocalDateTime.now());
        articleDTO.setDeleted(false);

        Article article = convertToEntity(articleDTO);

        Article saved = articleRepository.save(article);
        return convertToDTO(saved);
    }

    // ----------- UPDATE ARTICLE -----------
    public ArticleDTO updateArticle(Integer articleId, ArticleDTO updatedArticle) {
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        try {
            if (updatedArticle.getGallery() != null && !updatedArticle.getGallery().isEmpty()) {
                String galleryJson = objectMapper.writeValueAsString(updatedArticle.getGallery());
                existingArticle.setGallery(galleryJson);
            } else {
                existingArticle.setGallery("[]"); // default empty array
            }
        } catch (Exception e) {
            existingArticle.setGallery("[]");
        }

        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setContent(updatedArticle.getContent());
        existingArticle.setCoverImage(updatedArticle.getCoverImage());
        existingArticle.setAuthor(updatedArticle.getAuthor());
        existingArticle.setType(updatedArticle.getType());
        existingArticle.setCategory(updatedArticle.getCategory());
        existingArticle.setDate(LocalDateTime.now());

        Article saved = articleRepository.save(existingArticle);
        return convertToDTO(saved);
    }

    // ----------- PATCH ARTICLE -----------
    public ArticleDTO patchArticle(Integer articleId, ArticleDTO updatedArticle) {
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if(updatedArticle.getGallery() != null && !updatedArticle.getGallery().isEmpty()) {
            try {
                if (updatedArticle.getGallery() != null && !updatedArticle.getGallery().isEmpty()) {
                    String galleryJson = objectMapper.writeValueAsString(updatedArticle.getGallery());
                    existingArticle.setGallery(galleryJson);
                } else {
                    existingArticle.setGallery("[]"); // default empty array
                }
            } catch (Exception e) {
                existingArticle.setGallery("[]");
            }
        }

        if(updatedArticle.getTitle() != null && !updatedArticle.getTitle().isEmpty()) {
            existingArticle.setTitle(updatedArticle.getTitle());
        }
        if(updatedArticle.getContent() != null && !updatedArticle.getContent().isEmpty()) {
            existingArticle.setContent(updatedArticle.getContent());
        }
        if(updatedArticle.getCoverImage() != null && !updatedArticle.getCoverImage().isEmpty()) {
            existingArticle.setCoverImage(updatedArticle.getCoverImage());
        }
        if(updatedArticle.getAuthor() != null && !updatedArticle.getAuthor().isEmpty()) {
            existingArticle.setAuthor(updatedArticle.getAuthor());
        }
        if(updatedArticle.getType() != null && !updatedArticle.getType().isEmpty()) {
            existingArticle.setType(updatedArticle.getType());
        }
        if (updatedArticle.getCategory() != null) {
            existingArticle.setCategory(updatedArticle.getCategory());
        }
        if (updatedArticle.getDate() != null) {
            existingArticle.setDate(LocalDateTime.now());
        }
        if(updatedArticle.isDeleted()) {
            existingArticle.setDeleted(true);
            existingArticle.setDeletedAt(LocalDateTime.now());
        }
        else {
            existingArticle.setDeleted(false);
        }

        Article saved = articleRepository.save(existingArticle);
        return convertToDTO(saved);
    }

    public void deleteArticle(Integer articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức có id: " +  articleId));
        // Delete article in database
        if(article.isDeleted()) {
            articleRepository.delete(article);
        }
        else {
            throw new RuntimeException("Không thể xóa vĩnh viễn tin tức không nằm trong thùng rác");
        }
        // Delete image folder on cloudinary
        try {
            cloudinaryService.deleteFolder("hokinh/tin_tuc/" + articleId);
        }
        catch (Exception e) {
            throw new RuntimeException("Đã xảy ra lỗi khi xóa các hình ảnh liên quan đến tin tức. Hãy kiểm tra với id là " + articleId);
        }
    }
}
