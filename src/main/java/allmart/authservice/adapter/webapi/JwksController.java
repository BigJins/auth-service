package allmart.authservice.adapter.webapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * JWKS(JSON Web Key Set) 엔드포인트
 *
 * Gateway가 이 엔드포인트에서 공개키를 가져와 AccessToken 서명을 로컬 검증합니다.
 * 공개키이므로 외부 노출해도 무방합니다.
 *
 * 표준: RFC 7517 (JWK), RFC 7518 (JWA)
 */
@RestController
@RequiredArgsConstructor
public class JwksController {

    private final KeyPair rsaKeyPair;

    @GetMapping("/auth/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        RSAPublicKey pub = (RSAPublicKey) rsaKeyPair.getPublic();
        Map<String, Object> jwk = Map.of(
                "kty", "RSA",
                "use", "sig",
                "alg", "RS256",
                "kid", "allmart-key-1",
                "n", base64Url(pub.getModulus().toByteArray()),
                "e", base64Url(pub.getPublicExponent().toByteArray())
        );
        return Map.of("keys", List.of(jwk));
    }

    // BigInteger.toByteArray()는 부호 비트용 0x00을 앞에 붙일 수 있음 → JWK 표준에서는 제거
    private String base64Url(byte[] bytes) {
        if (bytes.length > 1 && bytes[0] == 0) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}