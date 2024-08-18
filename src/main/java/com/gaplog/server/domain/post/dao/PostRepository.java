package com.gaplog.server.domain.post.dao;

import com.gaplog.server.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    //Optional<Post> setMainPage();
    List<Post> findByCategoryId(Long categoryId);
    Optional<Post> findById(Long id);
    List<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);
}
