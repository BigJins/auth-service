package allmart.authservice.application.required;

import java.util.Optional;

public interface RefreshTokenStore {
    void save(String subject, String refreshToken, long ttlMillis);
    Optional<String> find(String subject);
    void delete(String subject);
}