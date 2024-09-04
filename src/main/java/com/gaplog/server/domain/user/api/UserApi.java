package com.gaplog.server.domain.user.api;

import com.gaplog.server.domain.comment.dto.response.CommentResponse;
import com.gaplog.server.domain.post.dto.response.MainPostResponse;
import com.gaplog.server.domain.user.application.UserService;
import com.gaplog.server.domain.user.dto.request.UserUpdateRequest;
import com.gaplog.server.domain.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gaplog.server.global.util.ApiUtil.getUserIdFromAuthentication;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @Operation(summary = "유저 정보 수정", description = "유저의 정보를 수정합니다.")
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateRequest userUpdateRequestDTO){
        try {
            Long userId = getUserIdFromAuthentication();
            UserResponse userUpdateResponseDTO = userService.updateUser(userId,userUpdateRequestDTO);
            return ResponseEntity.ok(userUpdateResponseDTO);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @Operation(summary = "유저 정보 조회", description = "유저의 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<UserResponse> getUser(){
        try {
            Long userId = getUserIdFromAuthentication();
            UserResponse userResponseDTO = userService.getUserInfo(userId);
            return ResponseEntity.ok(userResponseDTO);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "유저 탈퇴", description = "유저가 서비스를 탈퇴합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(){
        try{
            Long userId = getUserIdFromAuthentication();
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "유저가 작성한 게시글 조회", description = "유저가 작성한 게시글을 조회합니다.")
    @GetMapping("/posts")
    public ResponseEntity<List<MainPostResponse>> getUserPost() {
        try {
            Long userId = getUserIdFromAuthentication();
            List<MainPostResponse> mainPostResponses = userService.getUserPostInfo(userId);
            return ResponseEntity.ok(mainPostResponses);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "유저가 작성한 댓글 조회", description = "유저가 작성한 댓글을 조회합니다.")
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getUserComment() {
        try {
            Long userId = getUserIdFromAuthentication();
            List<CommentResponse> commentResponses = userService.getUserCommentInfo(userId);
            return ResponseEntity.ok(commentResponses);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
