package allmart.authservice.domain;

import allmart.authservice.domain.customer.Customer;
import allmart.authservice.domain.customer.LoginType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("이메일로 소비자 계정을 생성할 수 있다")
    void register_customer_by_email() {
        Customer customer = Customer.register("hong@allmart.com", "encodedPw", "홍길동");
        assertThat(customer.getEmail()).isEqualTo("hong@allmart.com");
        assertThat(customer.getName()).isEqualTo("홍길동");
        assertThat(customer.getLoginType()).isEqualTo(LoginType.EMAIL);
    }

    @Test
    @DisplayName("이메일이 null이면 예외가 발생한다")
    void register_null_email_throws() {
        assertThatThrownBy(() -> Customer.register(null, "encodedPw", "홍길동"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 예외가 발생한다")
    void register_blank_email_throws() {
        assertThatThrownBy(() -> Customer.register("", "encodedPw", "홍길동"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이름이 null이면 예외가 발생한다")
    void register_null_name_throws() {
        assertThatThrownBy(() -> Customer.register("hong@allmart.com", "encodedPw", null))
                .isInstanceOf(NullPointerException.class);
    }
}
