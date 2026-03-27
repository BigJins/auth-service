package allmart.authservice.domain;

import allmart.authservice.domain.member.Member;
import allmart.authservice.domain.member.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("유효한 정보로 판매자를 생성할 수 있다")
    void create_member_success() {
        Member member = Member.create("test@mart.com", "encodedPw123", "행복마트", MemberRole.MANAGER);
        assertThat(member.getEmail()).isEqualTo("test@mart.com");
        assertThat(member.getMartName()).isEqualTo("행복마트");
        assertThat(member.getRole()).isEqualTo(MemberRole.MANAGER);
    }

    @Test
    @DisplayName("이메일이 null이면 예외가 발생한다")
    void create_member_null_email_throws() {
        assertThatThrownBy(() -> Member.create(null, "encodedPw", "행복마트", MemberRole.MANAGER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 예외가 발생한다")
    void create_member_blank_email_throws() {
        assertThatThrownBy(() -> Member.create("  ", "encodedPw", "행복마트", MemberRole.MANAGER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("마트명이 없으면 예외가 발생한다")
    void create_member_blank_martName_throws() {
        assertThatThrownBy(() -> Member.create("test@mart.com", "encodedPw", "", MemberRole.MANAGER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마트명");
    }
}