package allmart.authservice.domain;

import allmart.authservice.config.JwtProperties;
import allmart.authservice.domain.token.AuthToken;
import allmart.authservice.domain.token.JwtProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.*;

class JwtProviderTest {

    // RSA 키 생성은 비용이 크므로 테스트 클래스당 1회만 수행
    private static KeyPair keyPair;
    private JwtProvider jwtProvider;

    @BeforeAll
    static void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        keyPair = gen.generateKeyPair();
    }

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(3600000L, 86400000L);
        jwtProvider = new JwtProvider(props, keyPair);
    }

    @Test
    @DisplayName("판매자 AccessToken을 생성하고 subject를 추출할 수 있다")
    void generate_and_extract_member_token() {
        String token = jwtProvider.generateAccessToken("admin@mart.com", "MEMBER");
        assertThat(jwtProvider.extractSubject(token)).isEqualTo("admin@mart.com");
        assertThat(jwtProvider.extractType(token)).isEqualTo("MEMBER");
    }

    @Test
    @DisplayName("고객 AccessToken의 type은 CUSTOMER다")
    void generate_customer_token_type() {
        String token = jwtProvider.generateAccessToken("12345678", "CUSTOMER");
        assertThat(jwtProvider.extractType(token)).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("유효한 토큰은 isValid가 true를 반환한다")
    void valid_token_returns_true() {
        String token = jwtProvider.generateAccessToken("test@mart.com", "MEMBER");
        assertThat(jwtProvider.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("위조된 토큰은 isValid가 false를 반환한다")
    void tampered_token_is_invalid() {
        String token = jwtProvider.generateAccessToken("test@mart.com", "MEMBER");
        assertThat(jwtProvider.isValid(token + "tampered")).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 isValid가 false를 반환한다")
    void expired_token_is_invalid() throws InterruptedException {
        JwtProperties shortProps = new JwtProperties(1L, 86400000L); // 1ms — 즉시 만료
        JwtProvider shortProvider = new JwtProvider(shortProps, keyPair);

        String token = shortProvider.generateAccessToken("test@mart.com", "MEMBER");
        Thread.sleep(10);
        assertThat(shortProvider.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("issue()는 AccessToken과 RefreshToken 모두 발급한다")
    void issue_returns_both_tokens() {
        AuthToken authToken = jwtProvider.issue("test@mart.com", "MEMBER");
        assertThat(authToken.accessToken()).isNotBlank();
        assertThat(authToken.refreshToken()).isNotBlank();
        assertThat(authToken.accessToken()).isNotEqualTo(authToken.refreshToken());
    }
}