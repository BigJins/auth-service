package allmart.authservice.application;

import allmart.authservice.application.provided.TokenRefresher;
import allmart.authservice.application.required.RefreshTokenStore;
import allmart.authservice.domain.token.AuthToken;
import allmart.authservice.domain.token.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService implements TokenRefresher {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    @Override
    public AuthToken refresh(String refreshToken) {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
        }
        String subject = jwtProvider.extractSubject(refreshToken);
        String stored = refreshTokenStore.find(subject)
                .orElseThrow(() -> new IllegalArgumentException("만료되거나 로그아웃된 토큰입니다."));
        if (!stored.equals(refreshToken)) {
            throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
        }
        // AccessToken만 새로 발급 (RefreshToken 재사용 — TTL은 Redis가 관리)
        String type = jwtProvider.extractType(refreshToken);
        if (type == null) type = "MEMBER";
        String newAccessToken = jwtProvider.generateAccessToken(subject, type);
        return new AuthToken(newAccessToken, refreshToken);
    }

    public void logout(String accessToken) {
        if (!jwtProvider.isValid(accessToken)) return; // 이미 만료된 토큰도 로그아웃 허용
        String subject = jwtProvider.extractSubject(accessToken);
        refreshTokenStore.delete(subject);
    }
}
