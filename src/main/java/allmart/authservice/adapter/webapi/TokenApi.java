package allmart.authservice.adapter.webapi;

import allmart.authservice.adapter.webapi.dto.TokenResponse;
import allmart.authservice.application.TokenService;
import allmart.authservice.application.provided.TokenRefresher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TokenApi {

    private final TokenRefresher tokenRefresher;
    private final TokenService tokenService;

    @PostMapping("/auth/refresh")
    public TokenResponse refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return TokenResponse.from(tokenRefresher.refresh(refreshToken));
    }

    @PostMapping("/auth/logout")
    @ResponseBody
    public void logout(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
        tokenService.logout(token);
    }
}