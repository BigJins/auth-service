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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalStateException("이미 가입된 이메일입니다: " + email);
        }
        Member member = Member.create(email, passwordEncoder.encode(rawPassword), martName, MemberRole.MANAGER);
        memberRepository.save(member);
        return issueAndStore(email, "MEMBER");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthToken login(String email, String rawPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(rawPassword, member.getEncodedPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return issueAndStore(email, "MEMBER");
    }

    private AuthToken issueAndStore(String subject, String type) {
        AuthToken token = jwtProvider.issue(subject, type);
        refreshTokenStore.save(subject, token.refreshToken(), jwtProperties.getRefreshTokenExpiry());
        return token;
    }
}