package allmart.authservice.adapter.webapi.dto;

import allmart.authservice.domain.token.AuthToken;

public record TokenResponse(String accessToken, String refreshToken) {
    public static TokenResponse from(AuthToken token) {
        return new TokenResponse(token.accessToken(), token.refreshToken());
    }
}