package allmart.authservice.application.provided;

import allmart.authservice.application.required.RefreshTokenStore;
import allmart.authservice.domain.token.AuthToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberAuthServiceTest {

    // Redis 없이 테스트하기 위한 인메모리 대체 구현체
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public RefreshTokenStore fakeRefreshTokenStore() {
            return new RefreshTokenStore() {
                private final Map<String, String> store = new ConcurrentHashMap<>();

                @Override
                public void save(String subject, String token, long ttlMillis) {
                    store.put(subject, token);
                }

                @Override
                public Optional<String> find(String subject) {
                    return Optional.ofNullable(store.get(subject));
                }

                @Override
                public void delete(String subject) {
                    store.remove(subject);
                }
            };
        }
    }

    MemberRegistrar memberRegistrar;
    MemberAuthenticator memberAuthenticator;

    MemberAuthServiceTest(MemberRegistrar memberRegistrar, MemberAuthenticator memberAuthenticator) {
        this.memberRegistrar = memberRegistrar;
        this.memberAuthenticator = memberAuthenticator;
    }

    @Test
    @DisplayName("이메일과 비밀번호로 회원가입하면 JWT가 발급된다")
    void register_returns_token() {
        AuthToken token = memberRegistrar.register("new@mart.com", "Password1!", "새마트");
        assertThat(token.accessToken()).isNotBlank();
        assertThat(token.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("중복 이메일로 회원가입하면 예외가 발생한다")
    void register_duplicate_email_throws() {
        memberRegistrar.register("dup@mart.com", "Password1!", "마트A");
        assertThatThrownBy(() -> memberRegistrar.register("dup@mart.com", "Password1!", "마트B"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 가입된 이메일");
    }

    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인하면 JWT가 발급된다")
    void login_with_correct_credentials_returns_token() {
        memberRegistrar.register("login@mart.com", "Password1!", "로그인마트");
        AuthToken token = memberAuthenticator.login("login@mart.com", "Password1!");
        assertThat(token.accessToken()).isNotBlank();
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인하면 예외가 발생한다")
    void login_with_wrong_password_throws() {
        memberRegistrar.register("wrong@mart.com", "Password1!", "마트");
        assertThatThrownBy(() -> memberAuthenticator.login("wrong@mart.com", "WrongPassword!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인하면 예외가 발생한다")
    void login_with_unknown_email_throws() {
        assertThatThrownBy(() -> memberAuthenticator.login("nobody@mart.com", "Password1!"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}