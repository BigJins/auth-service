package allmart.authservice.config;

import allmart.authservice.adapter.oauth2.KakaoOAuth2SuccessHandler;
import allmart.authservice.adapter.oauth2.KakaoOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoOAuth2UserService kakaoOAuth2UserService;
    private final KakaoOAuth2SuccessHandler kakaoOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 판매자 회원가입/로그인
                        .requestMatchers("/auth/members/**").permitAll()
                        // 카카오 OAuth2 플로우
                        .requestMatchers("/auth/customers/**", "/login/oauth2/**", "/oauth2/**").permitAll()
                        // 토큰 갱신/로그아웃
                        .requestMatchers("/auth/refresh", "/auth/logout").permitAll()
                        // Actuator
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // 카카오 로그인 시작 경로: GET /auth/customers/kakao → 카카오 인증 페이지 리다이렉트
                        .authorizationEndpoint(endpoint ->
                                endpoint.baseUri("/auth/customers"))
                        .redirectionEndpoint(endpoint ->
                                endpoint.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(kakaoOAuth2UserService))
                        .successHandler(kakaoOAuth2SuccessHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
