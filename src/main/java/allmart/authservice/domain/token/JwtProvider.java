package allmart.authservice.domain.token;

import allmart.authservice.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final KeyPair rsaKeyPair;  // RS256 — 개인키로 서명, 공개키로 검증

    public String generateAccessToken(String subject, String type) {
        return generateAccessToken(subject, type, null);
    }

    public String generateAccessToken(String subject, String type, Long uid) {
        var builder = Jwts.builder()
                .subject(subject)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiry()))
                .signWith(rsaKeyPair.getPrivate()); // RS256 자동 선택
        if (uid != null) {
            builder.claim("uid", uid);
        }
        return builder.compact();
    }

    private String generateRefreshToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiry()))
                .signWith(rsaKeyPair.getPrivate())
                .compact();
    }

    public AuthToken issue(String subject, String type) {
        return new AuthToken(generateAccessToken(subject, type), generateRefreshToken(subject));
    }

    public AuthToken issue(String subject, String type, Long uid) {
        return new AuthToken(generateAccessToken(subject, type, uid), generateRefreshToken(subject));
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((PublicKey) rsaKeyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}