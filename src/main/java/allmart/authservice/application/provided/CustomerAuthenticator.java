package allmart.authservice.application.provided;

import allmart.authservice.domain.token.AuthToken;

public interface CustomerAuthenticator {
    AuthToken login(String email, String rawPassword);
}
