package allmart.authservice.adapter.oauth2;

import allmart.authservice.application.required.CustomerRepository;
import allmart.authservice.domain.customer.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomerRepository customerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String kakaoId = String.valueOf(
                Objects.requireNonNull(oAuth2User.getAttribute("id"), "카카오 사용자 ID가 없습니다."));

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = Objects.requireNonNull(
                (Map<String, Object>) oAuth2User.getAttribute("kakao_account"),
                "카카오 계정 정보가 없습니다.");

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = Objects.requireNonNull(
                (Map<String, Object>) kakaoAccount.get("profile"),
                "카카오 프로필 정보가 없습니다.");

        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email"); // 동의 안 하면 null

        // 최초 로그인 시 자동 회원가입
        customerRepository.findByKakaoId(kakaoId).orElseGet(() -> {
            log.info("카카오 최초 로그인, 자동 회원가입: kakaoId={}", kakaoId);
            return customerRepository.save(Customer.ofKakao(kakaoId, nickname, email));
        });

        return oAuth2User;
    }
}