package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * {@link Email} 的單元測試。
 * 驗證電子郵件格式檢查與不可變性。
 */
class EmailTest {

    @Test
    void should_create_email_with_valid_format() {
        String validEmail = "user@example.com";
        Email email = new Email(validEmail);

        assertThat(email).isNotNull();
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @Test
    void should_throw_exception_when_value_is_null() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_exception_when_value_is_blank() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_exception_when_format_is_invalid() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void should_throw_exception_when_format_is_invalid_no_at_sign() {
        assertThatThrownBy(() -> new Email("userexample.com"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_throw_exception_when_format_is_invalid_no_domain() {
        assertThatThrownBy(() -> new Email("user@"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_be_immutable() {
        String validEmail = "user@example.com";
        Email email = new Email(validEmail);

        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @Test
    void should_equality_based_on_value() {
        String emailStr = "user@example.com";
        Email email1 = new Email(emailStr);
        Email email2 = new Email(emailStr);

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}
