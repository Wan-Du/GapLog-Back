package com.gaplog.server.domain.post.application;

import com.gaplog.server.domain.category.dao.CategoryRepository;
import com.gaplog.server.domain.category.domain.Category;
import com.gaplog.server.domain.post.dao.PostRepository;
import com.gaplog.server.domain.post.domain.Post;
import com.gaplog.server.domain.post.dto.request.*;
import com.gaplog.server.domain.post.dto.response.*;
import com.gaplog.server.domain.user.dao.UserRepository;
import com.gaplog.server.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    //기본 기능
    //post 작성
    /*To-Do
     * 카테고리 string -> entity로 변경
     * */
    public Post createPost(Long userId, String title, String content, String thumbnailUrl, Long categoryId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOpt.get();

        //category 확인 추가,, 확인 필수,,
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        Category category = categoryOpt.get();

        Post newPost = Post.of(title, content, category, thumbnailUrl, user);
        return postRepository.save(newPost);
    }

    //post 수정
    @Transactional
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest postUpdateRequestDTO) {
        Post post = postRepository.findById(postId).orElseThrow(()
                ->new IllegalArgumentException("Post not found: " + postId));

        Optional<Category> categoryOpt = categoryRepository.findById(postUpdateRequestDTO.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + postUpdateRequestDTO.getCategoryId());
        }
        Category category = categoryOpt.get();

        post.updateCategory(category);
        post.updateTitle(postUpdateRequestDTO.getTitle());
        post.updateContent(postUpdateRequestDTO.getContent());

        Post updatedPost = postRepository.save(post);
        return PostUpdateResponse.of(updatedPost);
    }

//    //post category 수정
//    @Transactional
//    public void updatePostCategory(Long postId, PostCategoryUpdateRequest postCategoryUpdateRequestDTO) {
//        Post post = postRepository.findById(postId).orElseThrow(()
//                ->new IllegalArgumentException("Post not found: " + postId));
//
//        Optional<Category> categoryOpt = categoryRepository.findById(postCategoryUpdateRequestDTO.getCategoryId());
//        if (categoryOpt.isEmpty()) {
//            throw new RuntimeException("User not found with id: " + postCategoryUpdateRequestDTO.getCategoryId());
//        }
//        Category category = categoryOpt.get();
//
//        post.updateCategory(category);
//        postRepository.save(post);
//
//        return;
//    }

    //post 삭제
    /*To-Do
     * post 삭제 기능 구현
     * */
    public void deletePost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found with id: " + postId);
        }

        Post post = postOpt.get();

        try{
            postRepository.delete(post);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    //조회 기능
    //메인화면 게시글 조회
    @Transactional
    public List<MainPostResponse> getMainRecentPostInfo() {
        List<Post> posts = postRepository.findTop20ByOrderByCreatedAtDesc(PageRequest.of(0, 20));

        // Post 엔티티를 PostResponse DTO로 변환
        return posts.stream()
                .map(MainPostResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MainPostResponse> getMainHotPostInfo() {
        List<Post> posts = postRepository.findAllByOrderByLikeCountDesc(PageRequest.of(0, 20));

        // Post 엔티티를 PostResponse DTO로 변환
        return posts.stream()
                .map(MainPostResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MainPostResponse> getMainFollowPostInfo(Long userId) {
        List<Post> posts = postRepository.findLatestPostsByFollowedUsers(userId);

        // 게시물들을 최신순으로 정렬하고 상위 20개만 반환
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .limit(20)
                .map(MainPostResponse::of)
                .collect(Collectors.toList());

    }

    //특정 게시물(선택된 게시물) 조회
    @Transactional
    public SelectedPostResponse getSelectedPostInfo(Long postId) {
        Post getSelectedPostInfo = postRepository.findById(postId).orElseThrow(()
                ->new IllegalArgumentException("Post not found: " + postId));
        return SelectedPostResponse.of(getSelectedPostInfo);
    }

    //카테고리별 조회
    /*
     * To-Do
     * 카테고리별 조회 작성
     * */
    @Transactional
    public List<FindByCategoryPostResponse> getFindByCategoryPostInfo(Long categoryId){
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if(categoryOpt.isPresent()){
            List<Post> posts = postRepository.findByCategoryId(categoryId);

            if(posts.isEmpty()){
                throw new RuntimeException("Post not found with this category id: " + categoryId);
            }
            else{
                return posts.stream()
                        .map(FindByCategoryPostResponse::of)
                        .collect(Collectors.toList());
            }
        }
        else {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
    }

    //키워드 조회
    /*
     * To-Do
     * 키워드별 조회 작성
     * */
    @Transactional
    public List<FindByKeywordPostResponse> getFindByKeywordPostInfo(String keyword) {
        List<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword);

        if(posts.isEmpty()){
            throw new RuntimeException("Post not found with keyword: " + keyword);
        }
        else{
            return posts.stream()
                    .map(FindByKeywordPostResponse::of)
                    .collect(Collectors.toList());
        }
    }


    //반응 수정 기능
    //좋아요 수정
    @Transactional
    public PostLikeUpdateResponse PostLikeUpdate(Long postId, PostLikeUpdateRequest postLikeUpdateRequestDTO) {
        Post post = postRepository.findById(postId).orElseThrow(()
                ->new IllegalArgumentException("Post not found: " + postId));
        if(postLikeUpdateRequestDTO.isLike()){
            post.increaseLikeCount();
        }
        else{
            post.decreaseLikeCount();
        }

        Post updatedPost = postRepository.save(post);
        return PostLikeUpdateResponse.of(updatedPost);
    }

    //진지 수정
    //3..까지만 저장된다..
    @Transactional
    public PostSeriousnessUpdateResponse PostSeriousnessUpdate(Long postId, PostSeriousnessUpdateRequest postJinjiUpdateRequestDTO) {
        Post post = postRepository.findById(postId).orElseThrow(()
                ->new IllegalArgumentException("Post not found: " + postId));
        if(postJinjiUpdateRequestDTO.isSeriousness()){
            if(post.getSeriousnessCount()<4){
                post.increaseSeriousnessCount();
            }
        }
        else{
            post.decreaseSeriousnessCount();
        }

        //isPrivate 게시물 비공개
        if(post.getSeriousnessCount()==4){
            post.updateIsPrivate();
        }

        Post updatedPost = postRepository.save(post);
        return PostSeriousnessUpdateResponse.of(updatedPost);
    }

    //스크랩 수정
    @Transactional
    public PostScrapUpdateResponse PostScrapUpdate(Long postId, PostScrapUpdateRequest postScrapUpdateRequestDTO) {
        Post post = postRepository.findById(postId).orElseThrow(()
                ->new IllegalArgumentException("Post not found: " + postId));
        if(postScrapUpdateRequestDTO.isScrap()){
            post.increaseScrapCount();
        }
        else{
            post.decreaseScrapCount();
        }

        Post updatedPost = postRepository.save(post);
        return PostScrapUpdateResponse.of(updatedPost);
    }

    @Transactional
    public void PostCommentCountUpdate(Long postId, Boolean bool) {
        Post post = postRepository.findById(postId).orElseThrow(()
                ->new IllegalArgumentException("Post not found: " + postId));
        if(bool){
            post.increaseCommentCount();
        }
        else{
            post.decreaseCommentCount();
        }

        postRepository.save(post);

        return;
    }

}
