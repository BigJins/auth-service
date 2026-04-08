package allmart.authservice.domain.member;

import allmart.authservice.config.SnowflakeGenerated;
import allmart.authservice.domain.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.requireNonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {

    /** 판매자 고유 ID — martId로도 사용 (mart-service 도입 전까지) */
    @Id
    @SnowflakeGenerated
    private Long memberId;

    private String email;

    private String encodedPassword;

    private String martName;

    private MemberRole role;

    public static Member create(String email, String encodedPassword, String martName, MemberRole role) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
        if (encodedPassword == null || encodedPassword.isBlank()) throw new IllegalArgumentException("비밀번호는 필수입니다.");
        if (martName == null || martName.isBlank()) throw new IllegalArgumentException("마트명은 필수입니다.");
        requireNonNull(role, "권한은 필수입니다.");

        Member member = new Member();
        member.email = email;
        member.encodedPassword = encodedPassword;
        member.martName = martName;
        member.role = role;
        return member;
        // memberId는 @SnowflakeGenerated가 persist 시 자동 생성
    }
}