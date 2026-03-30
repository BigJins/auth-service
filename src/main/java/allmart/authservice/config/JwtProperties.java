package allmart.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(long accessTokenExpiry, long refreshTokenExpiry) {}