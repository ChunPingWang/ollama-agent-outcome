package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectValidationTest {

    @Test
    void should_create_valid_email() {
        Email email = Email.of("user@domain.com");
        assertThat(email.address()).isEqualTo("user@domain.com");
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> Email.of("   "))
            .isInstanceOf(Exception.class);
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> Email.of("invalid-email"))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_reject_too_long_email() {
        String longEmail = "a".repeat(250) + "@b.c"; // 253 chars, close to limit but valid length-wise? 
        // Max is 254. Let's make it > 254.
        String tooLongEmail = "a".repeat(255) + "@b.c";
        
        assertThatThrownBy(() -> Email.of(tooLongEmail))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_create_valid_display_name() {
        DisplayName name = DisplayName.of("John Doe");
        assertThat(name.name()).isEqualTo("John Doe");
    }

    @Test
    void should_trim_and_validate_display_name() {
        DisplayName name = DisplayName.of("  John Doe  ");
        assertThat(name.name()).isEqualTo("John Doe");
    }

    @Test
    void should_reject_null_display_name() {
        assertThatThrownBy(() -> DisplayName.of(null))
            .isInstanceOf(Exception.class);
    }

    @Test
    void should_reject_blank_display_name() {
        assertThatThrownBy(() -> DisplayName.of("   "))
            .isInstanceOf(Exception.class);
    }

    @Test
    void should_reject_too_long_display_name() {
        String longName = "A".repeat(51);
        assertThatThrownBy(() -> DisplayName.of(longName))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("must not exceed 50 characters");
    }

    @Test
    void should_allow_null_bio() {
        Bio bio = Bio.of(null);
        assertThat(bio.content()).isNull();
    }

    @Test
    void should_create_valid_bio() {
        Bio bio = Bio.of("This is a short bio.");
        assertThat(bio.content()).isEqualTo("This is a short bio.");
    }

    @Test
    void should_trim_bio_content() {
        Bio bio = Bio.of("  Trimmed Bio  ");
        assertThat(bio.content()).isEqualTo("Trimmed Bio");
    }

    @Test
    void should_reject_too_long_bio() {
        String longBio = "B".repeat(501);
        assertThatThrownBy(() -> Bio.of(longBio))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("must not exceed 500 characters");
    }

    @Test
    void should_create_profile_with_all_fields() {
        UUID id = UUID.randomUUID();
        Email email = Email.of("test@example.com");
        DisplayName displayName = DisplayName.of("Tester");
        Bio bio = Bio.of("Testing domain objects");

        Profile profile = new Profile(id, email, displayName, bio);

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail().address()).isEqualTo("test@example.com");
        assertThat(profile.getDisplayName().name()).isEqualTo("Tester");
        assertThat(profile.getBio().content()).isEqualTo("Testing domain objects");
        assertThat(profile.isNew()).isTrue();
    }
}
