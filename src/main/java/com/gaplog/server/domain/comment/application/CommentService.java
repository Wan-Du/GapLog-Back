package com.gaplog.server.domain.comment.application;

import com.gaplog.server.domain.comment.dao.CommentLikeRepository;
import com.gaplog.server.domain.comment.dao.CommentRepository;
import com.gaplog.server.domain.comment.domain.Comment;
import com.gaplog.server.domain.comment.domain.CommentLike;
import com.gaplog.server.domain.comment.dto.response.CommentLikeUpdateResponse;
import com.gaplog.server.domain.comment.dto.response.CommentResponse;
import com.gaplog.server.domain.comment.dto.response.CommentUpdateResponse;
import com.gaplog.server.domain.post.application.PostService;
import com.gaplog.server.domain.post.dao.PostRepository;
import com.gaplog.server.domain.post.domain.Post;
import com.gaplog.server.domain.user.dao.UserRepository;
import com.gaplog.server.domain.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostService postService;

    @Transactional
    public CommentResponse createComment(Long postId, Long userId, String text, Long parentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Comment newComment = Comment.of(post, user, text, parentId);

        postService.PostCommentCountUpdate(postId, true);

        commentRepository.save(newComment);
        return CommentResponse.of(newComment);
    }

    @Transactional
    public CommentUpdateResponse updateComment(Long commentId, String text) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        comment.setText(text);
        commentRepository.save(comment);
        return CommentUpdateResponse.of(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        List<Comment> childComments = commentRepository.findByParentId(commentId);

        // Soft Delete
        // isDeleted == true 라면 댓글이 사용자에게 노출되지 않음
        // 하지만 repository 상에서 삭제 되진 않음

        // case 1: 부모 댓글 : 자식 댓글이 1개 이상 있는  -> isDeletedParent = ture
        // case 2: 부모 댓글 : 자식 댓글이 없는 (대댓글이 없는 댓글) -> isDeleted = true
        // case 3: 자식 댓글  -> isDeleted = true & 부모 댓글의 유일한 댓글인지 검사
        // -> 부모 댓글의 자식 댓글 중 삭제X 댓글이 1개라면(지금 삭제하려는 댓글) 부모 댓글의 isDeleted = true & 둘 다 save

        // case 1: 삭제하려는 댓글에게 자식 댓글이 있을 때
        if(!childComments.isEmpty()) {
            comment.setDeletedParent(true);
            commentRepository.save(comment);
        }
        else { // 삭제하려는 댓글에게 자식 댓글이 없을 때

            Comment parentComment;

            // 삭제하려는 댓글에게 부모 댓글이 있을 때
            if (comment.getParentId() != null) {
                parentComment = commentRepository.findById(comment.getParentId())
                        .orElseThrow(() -> new EntityNotFoundException("Parent not found with id: " + comment.getParentId()));

                // case 3: 부모 댓글의 삭제X된 자식이 1개(지금 삭제 요청이 들어온 댓글)이며, 부모가 이미 삭제상태인 댓글

                List<Comment> childCommentsList = commentRepository.findByParentId(parentComment.getId());
                long notDeletedCount = childCommentsList.stream()
                        .filter(c -> !c.getIsDeleted()) // isDeleted가 false인 댓글 필터링
                        .count(); // 개수 세기

                if (notDeletedCount == 1 && parentComment.getIsDeletedParent()) {
                    parentComment.setDeleted(true);
                    commentRepository.save(parentComment);
                }
            }
            //case 2 & case 3의 자식 댓글 setDeleted
            comment.setDeleted(true);
            commentRepository.save(comment);
            postService.PostCommentCountUpdate(comment.getPost().getId(),false);
        }
    }

    @Transactional
    public CommentLikeUpdateResponse updateLikeCount(Long userId, Long commentId) throws InterruptedException{
        while (true) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

                Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

                //좋아요가 이미 눌러져 있을때, delete Comment Like
                if (commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)){
                    commentLikeRepository.deleteByUserIdAndCommentId(userId,commentId);

                    if (comment.getLikeCount() > 0){
                        comment.setLikeCount(comment.getLikeCount() - 1);
                    }
                } //좋아요가 없을때, save Comment Like
                else{
                    CommentLike commentLike = CommentLike.of(user, comment);
                    commentLikeRepository.save(commentLike);

                    comment.setLikeCount(comment.getLikeCount() + 1);
                }

                commentRepository.save(comment);
                return CommentLikeUpdateResponse.of(comment);
            } catch (OptimisticLockingFailureException e) {
                // 충돌이 발생한 경우에 대한 처리 로직
                Thread.sleep(50);
            }
        }
    }
}