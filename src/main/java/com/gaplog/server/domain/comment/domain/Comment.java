package com.gaplog.server.domain.comment.domain;

import com.gaplog.server.domain.post.domain.Post;
import com.gaplog.server.domain.user.domain.User;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "parent_id")
    private Long parentId;

    @ColumnDefault("0")
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @ColumnDefault("FALSE")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ColumnDefault("FALSE")
    @Column(name = "is_deleted_parent", nullable = false)
    private Boolean isDeletedParent = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version // Optimistic Locking을 위한 버전 필드
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setDeletedParent(Boolean deletedParent) {
        isDeletedParent = deletedParent;
    }


    @Builder
    public Comment(Post post, User user, String text, Long parentId) {
        this.post = post;
        this.user = user;
        this.text = text;
        this.parentId = parentId;
    }

    public static Comment of(Post post, User user, String text, Long parentId) {
        return Comment.builder()
                .post(post)
                .user(user)
                .text(text)
                .parentId(parentId)
                .build();
    }

}