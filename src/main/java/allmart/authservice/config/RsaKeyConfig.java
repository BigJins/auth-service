package allmart.authservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * RSA 키 쌍 제공
 *
 * rsa.private-key 미설정 시: 임시 키 쌍 자동 생성 (재시작 시 기존 토큰 무효화됨)
 * 운영 환경: RSA_PRIVATE_KEY 환경변수로 PKCS8 PEM 형식의 개인키 주입
 *
 * 키 생성 명령:
 *   openssl genrsa -out private.pem 2048
 *   openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.pem -out private-pkcs8.pem
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RsaKeyConfig {

    private final RsaKeyProperties rsaKeyProperties;

    @Bean
    public KeyPair rsaKeyPair() {
        String pem = rsaKeyProperties.privateKey();
        if (pem == null || pem.isBlank()) {
            log.warn("RSA 개인키 미설정 — 임시 키 쌍 생성. 재시작 시 기존 AccessToken 검증 불가.");
            return generateEphemeral();
        }
        return loadFromPem(pem);
    }

    private KeyPair generateEphemeral() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA 키 생성 실패", e);
        }
    }

    private KeyPair loadFromPem(String pem) {
        try {
            String cleaned = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(cleaned);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(
                    new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("RSA PEM 파싱 실패. PKCS8 형식인지 확인하세요.", e);
        }
    }
}