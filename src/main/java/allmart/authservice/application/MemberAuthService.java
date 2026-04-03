package allmart.authservice.application;

import allmart.authservice.application.provided.MemberAuthenticator;
import allmart.authservice.application.provided.MemberRegistrar;
import allmart.authservice.application.required.MemberRepository;
import allmart.authservice.application.required.RefreshTokenStore;
import allmart.authservice.config.JwtProperties;
import allmart.authservice.domain.member.Member;
import allmart.authservice.domain.member.MemberRole;
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
public class MemberAuthService implements MemberRegistrar, MemberAuthenticator {

    private final MemberRepository memberRepository;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthToken register(String email, String rawPassword, String martName) {
        if (memberRepository.existsByEmail(email)) {
            log.warn("판매자 회원가입 실패 - 이미 가입된 이메일: {}", email);
            throw new IllegalStateException("이미 가입된 이메일입니다: " + email);
        }
        Member member = Member.create(email, passwordEncoder.encode(rawPassword), martName, MemberRole.MANAGER);
        Member saved = memberRepository.save(member);
        log.info("판매자 회원가입 완료: memberId={}, email={}, martName={}", saved.getMemberId(), email, martName);
        return issueAndStore(saved.getEmail(), "MEMBER", saved.getMemberId());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthToken login(String email, String rawPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("판매자 로그인 실패 - 존재하지 않는 이메일: {}", email);
                    return new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
                });
        if (!passwordEncoder.matches(rawPassword, member.getEncodedPassword())) {
            log.warn("판매자 로그인 실패 - 비밀번호 불일치: email={}", email);
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        log.info("판매자 로그인 성공: memberId={}, email={}", member.getMemberId(), email);
        return issueAndStore(member.getEmail(), "MEMBER", member.getMemberId());
    }

    private AuthToken issueAndStore(String subject, String type, Long uid) {
        AuthToken token = jwtProvider.issue(subject, type, uid);
        refreshTokenStore.save(subject, token.refreshToken(), jwtProperties.refreshTokenExpiry());
        return token;
    }
}
