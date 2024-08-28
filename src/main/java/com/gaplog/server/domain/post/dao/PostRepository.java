package com.gaplog.server.domain.post.dao;

import com.gaplog.server.domain.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    //Optional<Post> setMainPage();
    List<Post> findByCategoryId(Long categoryId);
    Optional<Post> findById(Long id);
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findTop20ByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);
    List<Post> findAllByOrderByLikeCountDesc(Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.user IN " +
            "(SELECT ur.followee FROM UserRelationships ur WHERE ur.follower.id = :userId) " +
            "AND p.createdAt = (SELECT MAX(p2.createdAt) FROM Post p2 WHERE p2.user = p.user)")
    List<Post> findLatestPostsByFollowedUsers(@Param("userId") Long userId);
    // 특정 유저가 작성한 게시물 조회
    List<Post> findByUserId(Long userId);
}
