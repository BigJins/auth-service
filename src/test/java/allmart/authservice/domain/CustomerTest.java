package allmart.authservice.domain;

import allmart.authservice.domain.customer.Customer;
import allmart.authservice.domain.customer.LoginType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("카카오 ID로 고객을 생성할 수 있다")
    void create_kakao_customer() {
        Customer customer = Customer.ofKakao("12345678", "홍길동", "hong@kakao.com");
        assertThat(customer.getKakaoId()).isEqualTo("12345678");
        assertThat(customer.getNickname()).isEqualTo("홍길동");
        assertThat(customer.getEmail()).isEqualTo("hong@kakao.com");
        assertThat(customer.getLoginType()).isEqualTo(LoginType.KAKAO);
    }

    @Test
    @DisplayName("이메일이 null이어도 고객을 생성할 수 있다 (동의 안 한 경우)")
    void create_kakao_customer_without_email() {
        Customer customer = Customer.ofKakao("12345678", "홍길동", null);
        assertThat(customer.getEmail()).isNull();
    }

    @Test
    @DisplayName("카카오 ID가 null이면 예외가 발생한다")
    void create_kakao_customer_null_id_throws() {
        assertThatThrownBy(() -> Customer.ofKakao(null, "홍길동", null))
                .isInstanceOf(NullPointerException.class);
    }
}