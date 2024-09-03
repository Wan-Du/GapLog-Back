package com.gaplog.server.domain.post.domain;

import com.gaplog.server.domain.category.domain.Category;
import com.gaplog.server.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Many = Post, User = One 한명의 유저는 여러개의 게시글을 쓸 수 있다.
    @JoinColumn(name="user_id") // foreign key (user_id) references User (id)
    private User user;  //참조할 테이블

    @ManyToOne // Many = Post, Category = One 한개의 카테고리는 여러개의 게시글을 포함할 수 있다.
    @JoinColumn(name="category_id") // foreign key (category_id) references Category (id)
    private Category category;

    //content
    @Column
    private String title;

    @Column(length = 10000)
    private String content;

    @Column(length = 100)
    private String thumbnailUrl;

    //time
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //count
    @Column
    private int likeCount;

    @Column
    private int SeriousnessCount;

    @Column
    private int scrapCount;

    @Column
    private int commentCount;

    @Column
    private boolean isPrivate;

    @Column
    private boolean isDeleted;

    @Column
    @Version
    private Long version;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCategory(Category category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void increaseSeriousnessCount() {this.SeriousnessCount++;}

    public void increaseScrapCount() {
        this.scrapCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void decreaseSeriousnessCount() {
        this.SeriousnessCount--;
    }

    public void decreaseScrapCount() {
        this.scrapCount--;
    }

    public void decreaseCommentCount() { this.commentCount--; }


    public void updateIsPrivate() {
        this.isPrivate = true;
    }

    public void updateIsDeleted() { this.isDeleted = true; }

    @Builder
    public Post(String title, String content, Category category, String thumbnailUrl, User user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        onCreate();
        this.likeCount = 0;
        this.SeriousnessCount = 0;
        this.scrapCount = 0;
        this.commentCount = 0;
        this.isPrivate = false;
        this.isDeleted = false;
        this.version = 0L;
        this.user = user;
    }

    public static Post of(String title, String content, Category category, String thumbnailUrl, User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .thumbnailUrl(thumbnailUrl)
                .user(user)
                .build();
    }
}