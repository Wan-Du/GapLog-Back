package com.gaplog.server.domain.category.api;

import com.gaplog.server.domain.auth.domain.oauth.OauthProvider;
import com.gaplog.server.domain.auth.dto.Tokens;
import com.gaplog.server.domain.auth.infra.TokenProvider;
import com.gaplog.server.domain.category.application.CategoryService;
import com.gaplog.server.domain.category.dto.response.CategoryTreeResponse;
import com.gaplog.server.domain.category.domain.Category;
import com.gaplog.server.domain.post.domain.Post;
import com.gaplog.server.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryApi.class)
@WithMockUser
class CategoryApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private TokenProvider tokenProvider;

    private String mockAuthentication(Long userId) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId.toString(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")
        ));

        Tokens tokens = tokenProvider.generateTokenDto(authentication);
        return tokens.getAccessToken();
    }

    @Test
    void getCategoryTree_ShouldReturnCategoryTree() throws Exception {
//        // Given
//        Long mockUserId = 1L;
//        String accessToken = mockAuthentication(mockUserId); // Set mock authentication
//
//        List<CategoryTreeResponse> categoryTree = Arrays.asList(
//                new CategoryTreeResponse(1L, "Category 1", List.of()),
//                new CategoryTreeResponse(2L, "Category 2", List.of())
//        );
//        when(categoryService.getCategoryTree(1L)).thenReturn(categoryTree);
//
//        // When & Then
//        mockMvc.perform(get("/api/category/users")
//                        .header("Authorization", "Bearer " + accessToken)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("Category 1"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].name").value("Category 2"));
//
//        // Verify
//        verify(categoryService, times(1)).getCategoryTree(1L);
    }

    @Test
    void getSubCategories_ShouldReturnPosts() throws Exception {
        // Given
        User mockUser = User.of("testNickName","testEmail", OauthProvider.GOOGLE,"testURL");
        Category mockCategory = new Category("Category 1", mockUser);
        Post post1 = new Post("First Post", "Content 1", mockCategory, "thumbnailUrl1", mockUser);
        Post post2 = new Post("Second Post", "Content 2", mockCategory, "thumbnailUrl2", mockUser);
        List<Post> posts = List.of(post1, post2);
        when(categoryService.getSubCategories(1L)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/category/1").with(csrf())) // CSRF 필요 없을 수 있음, 보안 설정에 따라 결정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(post1.getId()))
                .andExpect(jsonPath("$[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(post1.getContent()))
                .andExpect(jsonPath("$[1].id").value(post2.getId()))
                .andExpect(jsonPath("$[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$[1].content").value(post2.getContent()));

        // Verify
        verify(categoryService, times(1)).getSubCategories(1L);
    }

    @Test
    void addCategory_ShouldCreateCategory() throws Exception {
        // Given
        Category newCategory = Category.of("New Category", User.of("testNickName","testEmail",OauthProvider.GOOGLE,"testURL"));
        when(categoryService.addCategory(1L, 0L, "New Category")).thenReturn(newCategory);

        // When & Then
        mockMvc.perform(post("/api/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"ancestorId\":0,\"name\":\"New Category\"}").with(csrf()))
                .andExpect(status().isCreated())  // HTTP 상태 코드 201 검증
                .andExpect(jsonPath("$.id").value(newCategory.getId()))  // JSON 응답에서 id 값 검증
                .andExpect(jsonPath("$.name").value("New Category"));  // JSON 응답에서 name 값 검증

        // Verify
        verify(categoryService, times(1)).addCategory(1L, 0L, "New Category");
    }

    @Test
    void updateCategory_ShouldUpdateCategory() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"ancestorId\":2}").with(csrf()))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).updateCategory(1L, 2L);
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/category/1").with(csrf()))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}

