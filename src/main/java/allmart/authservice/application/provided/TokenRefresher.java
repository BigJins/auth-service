package allmart.authservice.application.provided;

import allmart.authservice.domain.token.AuthToken;

public interface TokenRefresher {
    AuthToken refresh(String refreshToken);
}