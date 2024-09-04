package com.gaplog.server.domain.category;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gaplog.server.domain.auth.domain.oauth.OauthProvider;
import com.gaplog.server.domain.category.application.CategoryService;
import com.gaplog.server.domain.category.dao.CategoryRepository;
import com.gaplog.server.domain.category.dao.ClosureCategoryRepository;
import com.gaplog.server.domain.category.domain.Category;
import com.gaplog.server.domain.category.domain.ClosureCategory;
import com.gaplog.server.domain.category.dto.request.CategorySaveRequest;
import com.gaplog.server.domain.category.dto.request.CategoryUpdateRequest;
import com.gaplog.server.domain.category.dto.response.CategoryTreeResponse;
import com.gaplog.server.domain.user.domain.User;
import com.gaplog.server.domain.user.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClosureCategoryRepository closureCategoryRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.of("testNickName","testEmail",OauthProvider.GOOGLE,"testURL"));
    }

    @Test
    @WithMockUser
    void testAddCategory() throws Exception {
        // given
        CategorySaveRequest request = new CategorySaveRequest(testUser.getId(), 0L, "Test Category");

        // when
        mockMvc.perform(post("/api/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated());

        // then
        List<Category> categories = categoryRepository.findByUser(testUser);
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getName()).isEqualTo("Test Category");
    }

    @Test
    @WithMockUser
    void testGetCategoryTree() throws Exception {
//        // Given
//        Category parentCategory = categoryService.addCategory(testUser.getId(),0L,"Parent Category");
//        categoryService.addCategory(testUser.getId(),parentCategory.getId(),"Child Category");
//
//        // When & Then
//        MvcResult result = mockMvc.perform(get("/api/category/users"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        List<CategoryTreeResponse> categoryTree = new ObjectMapper().registerModule(new JavaTimeModule())
//                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
//
//        // Then
//        CategoryTreeResponse parentCategoryTree = categoryTree.getFirst();
//        assertThat(parentCategory.getName()).isEqualTo("Parent Category");
//
//        List<CategoryTreeResponse> childrens = parentCategoryTree.getChildren();
//        assertThat(childrens.getFirst().getName()).isEqualTo("Child Category");
    }

    @Test
    @WithMockUser
    void testUpdateCategory() throws Exception {
        // Given
        Category category = categoryService.addCategory(testUser.getId(),0L,"Old Category");
        Category updateParentCategory = categoryService.addCategory(testUser.getId(),0L,"Update Parent Category");

        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest(category.getId(), updateParentCategory.getId());

        // When
        MvcResult result = mockMvc.perform(put("/api/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();
        Category updatedCategory = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(result.getResponse().getContentAsString(), Category.class);

        // Then : 업데이트 후 부모 노드 변경 확인
        assertThat(updatedCategory.getName()).isEqualTo("Old Category");
        List<ClosureCategory> updatedClosureCategory = closureCategoryRepository.findByIdDescendantId(updatedCategory.getId());
        assertThat(updatedClosureCategory).hasSize(2);
        assertThat(updatedClosureCategory.getFirst().getId().getAncestorId()).isEqualTo(updatedCategory.getId()); // self node
        assertThat(updatedClosureCategory.get(1).getId().getAncestorId()).isEqualTo(updateParentCategory.getId()); // updated parent node
    }

    @Test
    @WithMockUser
    void testDeleteCategory() throws Exception {
        // Given
        Category category = categoryService.addCategory(testUser.getId(),0L,"Category To Delete");

        // When
        mockMvc.perform(delete("/api/category/" + category.getId()))
                .andExpect(status().isOk());

        // Then
        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }

    // To Do : Post 응답 Test 추가
    @Test
    @WithMockUser
    void testGetSubCategories() throws Exception {
        // Given
        Category parentCategory = categoryService.addCategory(testUser.getId(),0L,"Parent Category");
        categoryService.addCategory(testUser.getId(),1L,"Child Category");

        // When & Then
        mockMvc.perform(get("/api/category/" + parentCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}