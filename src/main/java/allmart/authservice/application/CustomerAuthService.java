package allmart.authservice.application;

import allmart.authservice.application.provided.CustomerAuthenticator;
import allmart.authservice.application.provided.CustomerRegistrar;
import allmart.authservice.application.required.CustomerRepository;
import allmart.authservice.application.required.RefreshTokenStore;
import allmart.authservice.config.JwtProperties;
import allmart.authservice.domain.customer.Customer;
import allmart.authservice.domain.token.AuthToken;
import allmart.authservice.domain.token.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerAuthService implements CustomerRegistrar, CustomerAuthenticator {

    private final CustomerRepository customerRepository;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    /** 판매자(마트 관리자)가 소비자 계정을 생성 */
    @Override
    @Transactional
    public void register(String email, String rawPassword, String name) {
        if (customerRepository.existsByEmail(email)) {
            log.warn("소비자 계정 생성 실패 - 이미 가입된 이메일: {}", email);
            throw new IllegalStateException("이미 가입된 이메일입니다: " + email);
        }
        Customer customer = Customer.register(email, passwordEncoder.encode(rawPassword), name);
        Customer saved = customerRepository.save(customer);
        log.info("소비자 계정 생성 완료: customerId={}, email={}, name={}", saved.getCustomerId(), email, name);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthToken login(String email, String rawPassword) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("소비자 로그인 실패 - 존재하지 않는 이메일: {}", email);
                    return new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
                });
        if (!passwordEncoder.matches(rawPassword, customer.getEncodedPassword())) {
            log.warn("소비자 로그인 실패 - 비밀번호 불일치: email={}", email);
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        log.info("소비자 로그인 성공: customerId={}, email={}", customer.getCustomerId(), email);
        AuthToken token = jwtProvider.issue(email, "CUSTOMER", customer.getCustomerId());
        refreshTokenStore.save(email, token.refreshToken(), jwtProperties.refreshTokenExpiry());
        return token;
    }
}
