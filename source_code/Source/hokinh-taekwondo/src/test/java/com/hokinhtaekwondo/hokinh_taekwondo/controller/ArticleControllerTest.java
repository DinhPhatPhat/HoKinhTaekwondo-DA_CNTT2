package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.article.ArticleDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Tắt Security để tránh lỗi 401/403 khi test logic Controller
@WebMvcTest(value = ArticleController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    private ArticleDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = new ArticleDTO();
        sampleDTO.setId(1);
        sampleDTO.setTitle("Test Title");
    }

    @Test
    void testGetArticleHomepage() throws Exception {
        // Fix lỗi Page: Dùng PageImpl
        Mockito.when(articleService.getArticlesHomepage(anyInt(), anyInt(), anyString()))
                .thenReturn(new PageImpl<>(Collections.singletonList(sampleDTO)));

        mockMvc.perform(get("/api/article/homepage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Title"));
    }

    @Test
    void testCreateArticle_Success() throws Exception {
        Mockito.when(articleService.createArticle(any(ArticleDTO.class))).thenReturn(sampleDTO);

        mockMvc.perform(post("/api/article/admin/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteArticle_Success() throws Exception {
        // Đối với method void: Phải dùng doNothing()
        Mockito.doNothing().when(articleService).deleteArticle(1);

        mockMvc.perform(delete("/api/article/admin/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Xóa thành công")));
    }

    // --- PHẦN BỔ SUNG ĐỂ XỬ LÝ CÁC DÒNG 0% ---

    @Test
    void testGetArticleById_Success() throws Exception {
        Mockito.when(articleService.getArticleById(1)).thenReturn(sampleDTO);

        mockMvc.perform(get("/api/article/homepage/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllArticlesByCategory_Success() throws Exception {
        Mockito.when(articleService.getAllArticlesByCategory(anyInt()))
                .thenReturn(List.of(sampleDTO));

        mockMvc.perform(get("/api/article/admin/all-articles-by-category")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetAllDeletedArticles_Success() throws Exception {
        Mockito.when(articleService.getAllDeletedArticles()).thenReturn(List.of(sampleDTO));

        mockMvc.perform(get("/api/article/admin/all-deleted-articles"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateArticle_Success() throws Exception {
        Mockito.when(articleService.updateArticle(eq(1), any(ArticleDTO.class))).thenReturn(sampleDTO);

        mockMvc.perform(put("/api/article/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testPatchArticle_Success() throws Exception {
        Mockito.when(articleService.patchArticle(eq(1), any(ArticleDTO.class))).thenReturn(sampleDTO);

        mockMvc.perform(put("/api/article/admin/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());
    }

    // --- PHẦN BỔ SUNG ĐỂ XỬ LÝ KHỐI CATCH (Tăng từ 50% lên 100%) ---

    @Test
    void testCreateArticle_Exception() throws Exception {
        // Giả lập service ném ra lỗi để nhảy vào khối catch trong Controller
        Mockito.when(articleService.createArticle(any())).thenThrow(new RuntimeException("Database Error"));

        mockMvc.perform(post("/api/article/admin/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error creating article")));
    }

    // Test trường hợp lỗi cho Update để phủ dòng đỏ 106-108
    @Test
    void testUpdateArticle_InternalServerError() throws Exception {
        Mockito.when(articleService.updateArticle(anyInt(), any(ArticleDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(put("/api/article/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error updating article")));
    }

    // Test trường hợp lỗi cho Delete để phủ dòng đỏ 127-129
    @Test
    void testDeleteArticle_InternalServerError() throws Exception {
        Mockito.doThrow(new RuntimeException("Delete failed"))
                .when(articleService).deleteArticle(anyInt());

        mockMvc.perform(delete("/api/article/admin/delete/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Delete failed")));
    }

    // Test catch cho getAllArticlesByCategory
    @Test
    void testGetAllArticlesByCategory_InternalError() throws Exception {
        // Giả lập service ném Exception
        Mockito.when(articleService.getAllArticlesByCategory(anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/article/admin/all-articles-by-category")
                        .param("categoryId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error fetching articles by category")));
    }

    // Test catch cho getAllDeletedArticles
    @Test
    void testGetAllDeletedArticles_InternalError() throws Exception {
        Mockito.when(articleService.getAllDeletedArticles())
                .thenThrow(new RuntimeException("Server error"));

        mockMvc.perform(get("/api/article/admin/all-deleted-articles"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error fetching deleted articles")));
    }

    // Test catch cho patchArticle
    @Test
    void testPatchArticle_InternalError() throws Exception {
        Mockito.when(articleService.patchArticle(anyInt(), any(ArticleDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/article/admin/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error updating article")));
    }
}