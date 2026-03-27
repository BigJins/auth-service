package allmart.authservice.application.infrastructure;

import allmart.authservice.application.required.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String PREFIX = "refresh:";
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String subject, String refreshToken, long ttlMillis) {
        stringRedisTemplate.opsForValue().set(PREFIX + subject, refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<String> find(String subject) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(PREFIX + subject));
    }

    @Override
    public void delete(String subject) {
        stringRedisTemplate.delete(PREFIX + subject);
    }
}