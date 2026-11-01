package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;
import org.junit.jupiter.api.Test;

class EmailTest {

    private static final String VALID_EMAIL = "user@example.com";
    private static final String UPPERCASE_EMAIL = "USER@EXAMPLE.COM";
    private static final String INVALID_EMAIL = "not-an-email";

    @Test
    void should_create_valid_email() {
        Email email = new Email(VALID_EMAIL);
        assertThat(email.address()).isEqualTo(VALID_EMAIL.toLowerCase()); // 假設內部存為小寫以支援忽略大小寫比較，或保留原樣但 equals 忽略大小寫。任務說「忽略大小寫比較」，通常意味著 value 本身可能保持原樣或標準化。讓我們假設建構子會驗證並儲存。
    }

    @Test
    void should_reject_null_address() {
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_blank_address() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_invalid_format() {
        assertThatThrownBy(() -> new Email(INVALID_EMAIL))
            .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_be_equal_ignore_case() {
        Email e1 = new Email(VALID_EMAIL);
        Email e2 = new Email(UPPERCASE_EMAIL);
        assertThat(e1).isEqualTo(e2);
    }

    @Test
    void should_have_consistent_hash_code_for_same_email() {
        Email e1 = new Email(VALID_EMAIL);
        Email e2 = new Email(UPPERCASE_EMAIL);
        // 如果 equals 忽略大小寫，hashCode 也必須一致
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
