package com.gaplog.server.domain.post.api;


import com.gaplog.server.domain.post.application.PostImageService;
import com.gaplog.server.domain.post.application.PostService;
import com.gaplog.server.domain.post.domain.Post;
import com.gaplog.server.domain.post.dto.request.*;
import com.gaplog.server.domain.post.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "post API", description = "post API")
public class PostApi {
    private final PostService postService;
    private final PostImageService postImageService;

    @Autowired
    public PostApi(PostService postService, PostImageService postImageService) {
        this.postService = postService;
        this.postImageService = postImageService;
    }

    @PostMapping("/upload")
    public String upload(MultipartFile image) throws IOException {
        String imageUrl = postImageService.upload(image);
        //return "<img th:src=\"${"+imageUrl+"}\" alt=\"이미지\"/>";
        return imageUrl;
    }

    @PutMapping("/")
    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    public ResponseEntity<Post> putPost(@RequestBody PostRequest request) {
        Post response = postService.createPost(request.getUser(), request.getTitle(), request.getContent(), request.getThumbnailUrl(), request.getCategory());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{post_id}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public ResponseEntity<PostUpdateResponse> putUpdatePost(@PathVariable ("post_id") Long postId,@RequestBody PostUpdateRequest request) {
        PostUpdateResponse response = postService.updatePost(postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{post_id}")
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다.")
    public ResponseEntity<Void> deletePost(@PathVariable ("post_id") Long postId) {
        try {
            postService.deletePost(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{post_id}/likes")
    @Operation(summary = "게시글 좋아요 수정", description = "게시글의 좋아요수를 수정합니다.")
    public ResponseEntity<PostLikeUpdateResponse> putLikeUpdatePost(@PathVariable ("post_id") Long postId, @RequestBody PostLikeUpdateRequest request) {
        PostLikeUpdateResponse response = postService.PostLikeUpdate(postId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{post_id}/seriousness")
    @Operation(summary = "게시글 진지 수정", description = "게시글의 진지수를 수정합니다.")
    public ResponseEntity<PostSeriousnessUpdateResponse> putJSeriousnessUpdatePost(@PathVariable ("post_id") Long postId, @RequestBody PostSeriousnessUpdateRequest request) {
        PostSeriousnessUpdateResponse response = postService.PostSeriousnessUpdate(postId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{post_id}/scraps")
    @Operation(summary = "게시글 스크랩 수정", description = "게시글의 스크랩수를 수정합니다.")
    public ResponseEntity<PostScrapUpdateResponse> putScrapUpdatePost(@PathVariable ("post_id") Long postId, @RequestBody PostScrapUpdateRequest request) {
        PostScrapUpdateResponse response = postService.PostScrapUpdate(postId, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{post_id}")
    @Operation(summary = "특정 게시글 조회", description = "특정 게시글을 조회합니다.")
    public ResponseEntity<SelectedPostResponse> getSelectedPost(@PathVariable ("post_id") Long postId) {
        SelectedPostResponse response = postService.getSelectedPostInfo(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    @Operation(summary = "메인 페이지 최근 게시글 조회", description = "메인 페이지에 뜨는 최근 게시글의 정보를 얻습니다.")
    public ResponseEntity<List<MainPostResponse>> getMainRecentPost() {
        try {
            List<MainPostResponse> posts = postService.getMainRecentPostInfo();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/hot")
    @Operation(summary = "메인 페이지 인기 게시글 조회", description = "메인 페이지에 뜨는 인기 게시글의 정보를 얻습니다.")
    public ResponseEntity<List<MainPostResponse>> getMainHotPost() {
        try {
            List<MainPostResponse> posts = postService.getMainHotPostInfo();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/follow/{user_id}")
    @Operation(summary = "메인 페이지 팔로우하는 유저 게시글 조회", description = "메인 페이지에 뜨는 팔로우하는 유저 게시글의 정보를 얻습니다.")
    public ResponseEntity<List<MainPostResponse>> getMainFollowPost(@PathVariable("user_id") Long userId) {
        try {
            List<MainPostResponse> posts = postService.getMainFollowPostInfo(userId);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    //?@RequestParam Stirng keyword
    @GetMapping("/keywords/{keyword}")
    @Operation(summary = "키워드 게시글 조회", description = "입력된 키워드를 포함한 게시글의 정보를 얻습니다.")
    public ResponseEntity<List<FindByKeywordPostResponse>> getKeywordPost(@PathVariable("keyword") String keyword) {
        try {
            List<FindByKeywordPostResponse> posts = postService.getFindByKeywordPostInfo(keyword);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/categories/{category_id}")
    @Operation(summary = "카테고리 게시글 조회", description = "입력된 카테고리에 포함된 게시글의 정보를 얻습니다.")
    public ResponseEntity<List<FindByCategoryPostResponse>> getCategoryPost(@PathVariable ("category_id") Long categoryId) {
        try {
            List<FindByCategoryPostResponse> posts = postService.getFindByCategoryPostInfo(categoryId);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
