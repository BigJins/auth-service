package allmart.authservice.adapter.webapi;

import allmart.authservice.adapter.webapi.dto.MemberLoginRequest;
import allmart.authservice.adapter.webapi.dto.MemberSignupRequest;
import allmart.authservice.adapter.webapi.dto.TokenResponse;
import allmart.authservice.application.provided.MemberAuthenticator;
import allmart.authservice.application.provided.MemberRegistrar;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/members")
@RequiredArgsConstructor
public class MemberAuthApi {

    private final MemberRegistrar memberRegistrar;
    private final MemberAuthenticator memberAuthenticator;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse signup(@Valid @RequestBody MemberSignupRequest request) {
        return TokenResponse.from(
                memberRegistrar.register(request.email(), request.password(), request.martName())
        );
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody MemberLoginRequest request) {
        return TokenResponse.from(
                memberAuthenticator.login(request.email(), request.password())
        );
    }
}