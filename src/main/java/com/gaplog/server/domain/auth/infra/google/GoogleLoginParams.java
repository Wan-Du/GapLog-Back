package com.gaplog.server.domain.auth.infra.google;

import com.gaplog.server.domain.auth.domain.oauth.OauthLoginParams;
import com.gaplog.server.domain.auth.domain.oauth.OauthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginParams implements OauthLoginParams {
    private String authorizationCode;

    @Override
    public OauthProvider oauthProvider() {
        return OauthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
