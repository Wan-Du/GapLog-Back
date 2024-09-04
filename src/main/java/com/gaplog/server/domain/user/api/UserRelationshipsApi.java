package com.gaplog.server.domain.user.api;

import com.gaplog.server.domain.user.application.UserRelationshipsService;
import com.gaplog.server.domain.user.dto.request.UpdateFollowRequest;
import com.gaplog.server.domain.user.dto.UserRelationshipsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gaplog.server.global.util.ApiUtil.getUserIdFromAuthentication;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User-relationships", description = "User-relationships API")
public class UserRelationshipsApi {

    private final UserRelationshipsService userRelationshipsService;

    @Operation(summary = "유저의 팔로워 목록 조회", description = "특정 유저를 팔로우 하고 있는 목록을 조회합니다.")
    @GetMapping("/followers")
    public ResponseEntity<List<UserRelationshipsDto>> getFollowers() {
        try{
            Long userId = getUserIdFromAuthentication();
            List<UserRelationshipsDto> followers = userRelationshipsService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "유저의 팔로우 목록 조회", description = "특정 유저가 팔로우 하고 있는 목록을 조회합니다.")
    @GetMapping("/followees")
    public ResponseEntity<List<UserRelationshipsDto>> getFollowees() {
        try{
            Long userId = getUserIdFromAuthentication();
            List<UserRelationshipsDto> followees = userRelationshipsService.getFollowees(userId);
            return ResponseEntity.ok(followees);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @Operation(summary = "유저의 팔로우 목록 수정", description = "유저가 팔로우 하고 있는 목록을 수정합니다.")
    @PutMapping("/follow")
    public ResponseEntity<String> updateFollow(@RequestBody UpdateFollowRequest updateFollowRequestDTO){

        String action = updateFollowRequestDTO.getAction();
        Long targetId = updateFollowRequestDTO.getTargetId();

        try {
            Long userId = getUserIdFromAuthentication();
            String result = userRelationshipsService.updateFollow(userId, targetId, action);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
