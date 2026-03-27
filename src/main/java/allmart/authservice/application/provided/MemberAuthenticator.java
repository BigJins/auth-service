package allmart.authservice.application.provided;

import allmart.authservice.domain.token.AuthToken;

public interface MemberAuthenticator {
    AuthToken login(String email, String rawPassword);
}