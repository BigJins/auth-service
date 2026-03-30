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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalStateException("이미 가입된 이메일입니다: " + email);
        }
        Customer customer = Customer.register(email, passwordEncoder.encode(rawPassword), name);
        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthToken login(String email, String rawPassword) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(rawPassword, customer.getEncodedPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        AuthToken token = jwtProvider.issue(email, "CUSTOMER", customer.getCustomerId());
        refreshTokenStore.save(email, token.refreshToken(), jwtProperties.refreshTokenExpiry());
        return token;
    }
}
