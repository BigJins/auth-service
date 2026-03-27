package allmart.authservice.application.provided;

import allmart.authservice.domain.token.AuthToken;

public interface MemberRegistrar {
    AuthToken register(String email, String rawPassword, String martName);
}