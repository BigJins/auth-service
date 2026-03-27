package allmart.authservice.domain.member;

import allmart.authservice.domain.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Table(name = "tbl_member")
@Getter
public class Member extends AbstractEntity {

    @Id
    private String email;

    @Column(nullable = false)
    private String encodedPassword;

    @Column(nullable = false)
    private String martName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    protected Member() {}

    private Member(String email, String encodedPassword, String martName, MemberRole role) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.martName = martName;
        this.role = role;
    }

    public static Member create(String email, String encodedPassword, String martName, MemberRole role) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
        if (encodedPassword == null || encodedPassword.isBlank()) throw new IllegalArgumentException("비밀번호는 필수입니다.");
        if (martName == null || martName.isBlank()) throw new IllegalArgumentException("마트명은 필수입니다.");
        Objects.requireNonNull(role, "권한은 필수입니다.");
        return new Member(email, encodedPassword, martName, role);
    }
}
