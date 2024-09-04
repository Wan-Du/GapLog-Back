package com.gaplog.server.domain.auth.api;

import com.gaplog.server.domain.auth.application.OauthLoginService;
import com.gaplog.server.domain.auth.dto.Tokens;
import com.gaplog.server.domain.auth.dto.response.TokenResponse;
import com.gaplog.server.domain.auth.infra.google.GoogleLoginParams;
import com.gaplog.server.domain.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {

    private final OauthLoginService oauthLoginService;

    @PostMapping("/google")
    public ResponseEntity<?> loginGoogle(@RequestBody GoogleLoginParams params, HttpServletResponse response) {
        Tokens tokens = oauthLoginService.login(params);
        TokenResponse tokenResponseDto = JwtUtil.setJwtResponse(response, tokens);
        return ResponseEntity.ok(tokenResponseDto);
    }
}
