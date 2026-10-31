package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    void profileId_should_hold_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void email_should_reject_null_address() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email address must not be null");
    }

    @Test
    void email_should_reject_empty_address() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void email_should_reject_invalid_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void email_should_accept_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.address()).isEqualTo("user@example.com");
    }

    @Test
    void displayName_can_be_null_or_blank() {
        DisplayName emptyName = new DisplayName("");
        assertThat(emptyName.name()).isEmpty(); // 內部處理為空或null，視實作而定，此處假設trim後若無內容則視為空
        
        DisplayName normalName = new DisplayName("John Doe");
        assertThat(normalName.name()).contains("John Doe");
    }

    @Test
    void bio_should_enforce_length_limit() {
        String longBio = "a".repeat(501);
        assertThatThrownBy(() -> new Bio(longBio))
                .isInstanceOf(BioTooLongException.class)
                .hasMessageContaining("bio must not exceed 500 characters");
    }

    @Test
    void bio_should_allow_empty_content() {
        Bio bio = new Bio(null);
        assertThat(bio.content()).isNull();
        
        Bio emptyBio = new Bio("");
        assertThat(emptyBio.content()).isEmpty();
    }
}
