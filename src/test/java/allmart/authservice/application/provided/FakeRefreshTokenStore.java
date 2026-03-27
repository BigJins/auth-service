package allmart.authservice.application.provided;

import allmart.authservice.application.required.RefreshTokenStore;
import org.springframework.boot.test.context.TestComponent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 통합 테스트용 인메모리 RefreshTokenStore.
 * 실제 Redis 없이 MemberAuthService 유스케이스를 테스트할 수 있도록 대체 구현체를 제공한다.
 */
@TestComponent
public class FakeRefreshTokenStore implements RefreshTokenStore {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void save(String subject, String refreshToken, long ttlMillis) {
        store.put(subject, refreshToken);
    }

    @Override
    public Optional<String> find(String subject) {
        return Optional.ofNullable(store.get(subject));
    }

    @Override
    public void delete(String subject) {
        store.remove(subject);
    }
}