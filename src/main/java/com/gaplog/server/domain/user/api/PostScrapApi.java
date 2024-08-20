package com.gaplog.server.domain.user.api;

import com.gaplog.server.domain.user.application.PostScrapService;
import com.gaplog.server.domain.user.dto.request.PostScrapRequest;
import com.gaplog.server.domain.user.dto.response.PostScrapResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/{user_id}/scraps")
@RequiredArgsConstructor
@Tag(name = "Post-scrap", description = "Post-scrap API")
public class PostScrapApi {
    private final PostScrapService postScrapService;

    @Operation(summary = "유저가 스크랩한 게시물 조회", description = "유저가 스크랩한 게시물을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PostScrapResponse>> getScrapPost(
            @PathVariable("user_id") Long userId) {
        try {
            List<PostScrapResponse> responseDTOs = postScrapService.getScrapPosts(userId);
            return ResponseEntity.ok(responseDTOs);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

}
