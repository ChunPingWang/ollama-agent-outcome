package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileValueObjectTest {

    @Test
    void should_create_valid_profile_id() {
        String uuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.getValue()).isEqualTo(uuid);
    }

    @Test
    void should_reject_invalid_profile_id_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_normalize_email_to_lowercase() {
        Email email = new Email("User@Example.COM");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_allow_empty_display_name() {
        DisplayName name = new DisplayName("");
        assertThat(name.isEmpty()).isTrue();
    }

    @Test
    void should_trim_display_name() {
        DisplayName name = new DisplayName("  John Doe  ");
        assertThat(name.getName()).isEqualTo("John Doe");
    }
}
