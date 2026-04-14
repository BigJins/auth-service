package allmart.authservice.adapter.webapi;

import allmart.authservice.adapter.webapi.dto.CustomerLoginRequest;
import allmart.authservice.adapter.webapi.dto.CustomerRegisterRequest;
import allmart.authservice.adapter.webapi.dto.CustomerSummaryResponse;
import allmart.authservice.adapter.webapi.dto.TokenResponse;
import allmart.authservice.application.provided.CustomerAuthenticator;
import allmart.authservice.application.provided.CustomerRegistrar;
import allmart.authservice.application.required.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/customers")
@RequiredArgsConstructor
public class CustomerAuthApi {

    private final CustomerRegistrar customerRegistrar;
    private final CustomerAuthenticator customerAuthenticator;
    private final CustomerRepository customerRepository;

    /**
     * 소비자 계정 등록 — 판매자(마트 관리자)만 호출 가능
     * Gateway에서 MEMBER 역할 검증 후 X-User-Type 헤더 주입
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(
            @RequestHeader(value = "X-User-Type", required = false) String userType,
            @Valid @RequestBody CustomerRegisterRequest request) {
        if (!"MEMBER".equals(userType)) {
            throw new IllegalStateException("판매자 권한이 필요합니다.");
        }
        customerRegistrar.register(request.email(), request.password(), request.name(),
                request.zipCode(), request.roadAddress(), request.detailAddress());
        return Map.of("message", "소비자 계정이 등록되었습니다.", "email", request.email());
    }

    /** 소비자 로그인 */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody CustomerLoginRequest request) {
        return TokenResponse.from(
                customerAuthenticator.login(request.email(), request.password())
        );
    }

    /**
     * 소비자 목록 조회 — 판매자(마트 관리자)만 호출 가능
     */
    @GetMapping
    public List<CustomerSummaryResponse> findAll(
            @RequestHeader(value = "X-User-Type", required = false) String userType) {
        if (!"MEMBER".equals(userType)) {
            throw new IllegalStateException("판매자 권한이 필요합니다.");
        }
        return customerRepository.findAll().stream()
                .map(CustomerSummaryResponse::of)
                .toList();
    }
}
