package com.gaplog.server.domain.user.dto.response;

import com.gaplog.server.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PostScrapResponse {
    // post의 정보도 가져올 수 있도록 함

    private Long scrapId;
    private Long postId;
    private String title;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int seriousnessCount;

    public static PostScrapResponse of(Long scrapId, Post post) {
        return PostScrapResponse.builder()
                .scrapId(scrapId)
                .postId(post.getId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(post.getLikeCount())
                .seriousnessCount(post.getSeriousnessCount())
                .build();
    }
}
