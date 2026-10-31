package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void should_create_valid_profile_id() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.getId()).isEqualTo(uuid);
    }

    @Test
    void should_reject_null_profile_id() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class);
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class);
                
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName name = new DisplayName(null);
        assertThat(name.getValue()).isNull();
    }

    @Test
    void should_allow_empty_string_display_name() {
        DisplayName name = new DisplayName("");
        assertThat(name.getValue()).isEmpty();
    }

    @Test
    void should_reject_whitespace_only_display_name() {
        assertThatThrownBy(() -> new DisplayName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("whitespace");
    }
}
