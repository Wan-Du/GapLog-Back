package com.gaplog.server.domain.post.dto.response;

import com.gaplog.server.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SelectedPostResponse {

    //comment
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int jinjiCount;
    private int scrapCount;
    private int commentCount;

    public static SelectedPostResponse of(Post post) {
        return SelectedPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .jinjiCount(post.getSeriousnessCount())
                .scrapCount(post.getScrapCount())
                .commentCount(post.getCommentCount())
                .createdAt(LocalDateTime.parse(post.getCreatedAt().toString()))
                .updatedAt(LocalDateTime.parse(post.getUpdatedAt().toString()))
                .build();
    }
}
