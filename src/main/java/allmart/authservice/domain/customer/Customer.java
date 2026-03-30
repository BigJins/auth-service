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
    private String email;

    @Column(nullable = false)
    private String encodedPassword;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    protected Customer() {}

    private Customer(String email, String encodedPassword, String name, LoginType loginType) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
        this.loginType = loginType;
    }

    /** 판매자(마트 관리자)가 소비자 계정을 등록할 때 사용 */
    public static Customer register(String email, String encodedPassword, String name) {
        Objects.requireNonNull(email, "이메일은 필수입니다.");
        if (email.isBlank()) throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        Objects.requireNonNull(encodedPassword, "비밀번호는 필수입니다.");
        Objects.requireNonNull(name, "이름은 필수입니다.");
        return new Customer(email, encodedPassword, name, LoginType.EMAIL);
    }
}
