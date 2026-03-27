package allmart.authservice.domain.token;

public record AuthToken(String accessToken, String refreshToken) {
    public AuthToken {
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("accessToken은 필수입니다.");
        if (refreshToken == null || refreshToken.isBlank()) throw new IllegalArgumentException("refreshToken은 필수입니다.");
    }
}