package allmart.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    /**
     * 로컬 개발 전용: Sentinel 대신 localhost:6379 직접 연결
     *
     * 문제: Docker Sentinel이 master 주소로 컨테이너 내부 IP(172.x.x.x)를 반환하는데
     *       호스트에서 실행 중인 Spring 앱은 해당 IP에 접근 불가 (Docker Desktop 네트워크 제약)
     * 해결: local 프로파일에서 config-server의 Sentinel 설정을 무시하고
     *       포트 매핑된 localhost:6379로 직접 연결
     */
    @Profile("local")
    @Primary
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
