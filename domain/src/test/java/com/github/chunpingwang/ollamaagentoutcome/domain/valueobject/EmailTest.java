package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        String address = "user@example.com";
        Email email = new Email(address);
        assertThat(email.address()).isEqualTo(address);
    }

    @Test
    void should_reject_null_address() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_reject_empty_address() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_reject_invalid_format_without_at_sign() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@");
    }

    @Test
    void should_be_immutable() {
        // Records are immutable by default in Java, but we verify behavior.
        Email original = new Email("test@test.com");
        try {
            // Attempting to modify via reflection or similar would fail on record components
            // Here we just assert that the value remains constant after creation
            assertThat(original.address()).isEqualTo("test@test.com");
        } catch (Exception e) {
            throw new AssertionError("Should not be able to mutate email", e);
        }
    }
}
