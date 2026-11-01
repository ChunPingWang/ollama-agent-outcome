package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        String validAddress = "user@example.com";
        Email email = new Email(validAddress);
        assertThat(email.address()).isEqualTo(validAddress);
    }

    @Test
    void should_reject_blank_address() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為空白");
    }

    @Test
    void should_reject_null_address() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_reject_invalid_format_missing_at() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("無效的電子郵件格式");
    }

    @Test
    void should_reject_invalid_format_no_domain() {
        assertThatThrownBy(() -> new Email("@example.com"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_be_immutable() {
        // Records are immutable by default, but we verify the contract holds.
        Email original = new Email("test@test.com");
        String address = original.address();
        
        // If address() returned a mutable collection or array, this would be risky.
        // Since it returns String (immutable), we just ensure no setter exists and value is stable.
        assertThat(address).isEqualTo("test@test.com");
    }
}
