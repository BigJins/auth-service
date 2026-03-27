package allmart.authservice.domain.customer;

import allmart.authservice.domain.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Table(name = "tbl_customer")
@Getter
public class Customer extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(nullable = false)
    private String nickname;

    private String email; // 카카오 이메일 (nullable — 동의 안 할 수 있음)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    protected Customer() {}

    private Customer(String kakaoId, String nickname, String email, LoginType loginType) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
        this.loginType = loginType;
    }

    public static Customer ofKakao(String kakaoId, String nickname, String email) {
        Objects.requireNonNull(kakaoId, "카카오 ID는 필수입니다.");
        if (kakaoId.isBlank()) throw new IllegalArgumentException("카카오 ID는 비어있을 수 없습니다.");
        Objects.requireNonNull(nickname, "닉네임은 필수입니다.");
        return new Customer(kakaoId, nickname, email, LoginType.KAKAO);
    }
}