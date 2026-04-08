package allmart.authservice.domain.customer;

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
public class Customer extends AbstractEntity {

    @Id
    @SnowflakeGenerated
    private Long customerId;

    private String email;

    private String encodedPassword;

    private String name;

    private LoginType loginType;

    /** 판매자(마트 관리자)가 소비자 계정을 등록할 때 사용 */
    public static Customer register(String email, String encodedPassword, String name) {
        requireNonNull(email, "이메일은 필수입니다.");
        if (email.isBlank()) throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        requireNonNull(encodedPassword, "비밀번호는 필수입니다.");
        requireNonNull(name, "이름은 필수입니다.");

        Customer customer = new Customer();
        customer.email = email;
        customer.encodedPassword = encodedPassword;
        customer.name = name;
        customer.loginType = LoginType.EMAIL;
        return customer;
    }
}