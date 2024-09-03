package com.gaplog.server.domain.user.api;

import com.gaplog.server.domain.user.application.SeriousnessService;
import com.gaplog.server.domain.user.dto.SeriousnessDto;
import com.gaplog.server.domain.user.dto.response.SeriousnessFieldResponse;
import com.gaplog.server.domain.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/{user_id}/seriousness")
@Tag(name = "Seriousness", description = "Seriousness API")
public class SeriousnessApi {

    private final SeriousnessService seriousnessService;

    @GetMapping
    @Operation(summary = "유저 진지 티어 조회", description = "유저의 진지 티어 정보를 조회합니다.")
    public ResponseEntity<SeriousnessDto> getSeriousness(@PathVariable("user_id") Long userId) {
        try {
            SeriousnessDto seriousnessDTO = seriousnessService.getSeriousness(userId);
            return ResponseEntity.ok(seriousnessDTO);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    @Operation(summary = "유저 티어 업데이트", description = "유저의 진지 버튼 누적 수에 따라 티어를 업데이트합니다.")
    public ResponseEntity<Void> updateUserTier(@PathVariable("user_id") Long userId) {
        seriousnessService.updateSeriousnessTier(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seriousness-field")
    @Operation(summary = "완두밭 데이터 조회", description = "유저가 완두밭 데이터를 조회합니다.")
    public ResponseEntity<List<SeriousnessFieldResponse>> getSeriousnessFieldData(@PathVariable("user_id") Long userId) {
        try {
            List<SeriousnessFieldResponse> seriousnessFieldResponses = seriousnessService.getSeriousnessField(userId);
            return ResponseEntity.ok(seriousnessFieldResponses);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
