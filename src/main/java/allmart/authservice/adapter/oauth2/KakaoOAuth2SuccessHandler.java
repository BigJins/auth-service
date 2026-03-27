package allmart.authservice.adapter.oauth2;

import allmart.authservice.application.required.RefreshTokenStore;
import allmart.authservice.config.JwtProperties;
import allmart.authservice.domain.token.AuthToken;
import allmart.authservice.domain.token.JwtProvider;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenStore refreshTokenStore;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String kakaoId = String.valueOf(
                Objects.requireNonNull(oAuth2User.getAttribute("id"), "카카오 사용자 ID가 없습니다."));

        AuthToken token = jwtProvider.issue(kakaoId, "CUSTOMER");
        refreshTokenStore.save(kakaoId, token.refreshToken(), jwtProperties.getRefreshTokenExpiry());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
                Map.of("accessToken", token.accessToken(), "refreshToken", token.refreshToken())
        ));
    }
}