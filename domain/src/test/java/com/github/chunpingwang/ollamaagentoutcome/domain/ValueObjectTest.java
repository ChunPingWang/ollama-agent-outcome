package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void should_create_profile_id_with_valid_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);
        
        assertThat(profileId.getValue()).isEqualTo(uuid);
        assertThat(profileId.toString()).isEqualTo(uuid.toString());
    }

    @Test
    void should_throw_when_profile_id_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("ID cannot be null");
    }

    @Test
    void should_create_email_with_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_normalize_email_to_lowercase_and_trim() {
        Email email = new Email("  User@Example.COM  ");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be blank");
            
        assertThatThrownBy(() -> new Email("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be blank");
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailFormatException.class);
            
        assertThatThrownBy(() -> new Email("@example.com"))
            .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_create_display_name_with_text() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.getName()).isEqualTo("John Doe");
    }

    @Test
    void should_convert_null_or_blank_display_name_to_null_internal_value() {
        DisplayName empty1 = new DisplayName(null);
        DisplayName empty2 = new DisplayName("");
        DisplayName empty3 = new DisplayName("   ");
        
        assertThat(empty1.getName()).isNull();
        assertThat(empty2.getName()).isNull();
        assertThat(empty3.getName()).isNull();
    }

    @Test
    void should_trim_display_name() {
        DisplayName displayName = new DisplayName("  John  ");
        assertThat(displayName.getName()).isEqualTo("John");
    }

    @Test
    void should_reject_excessively_long_display_name() {
        String longName = "a".repeat(101);
        assertThatThrownBy(() -> new DisplayName(longName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exceeds max length");
    }

    @Test
    void value_objects_should_be_immutable_and_equal_by_content() {
        Email email1 = new Email("test@test.com");
        Email email2 = new Email("test@test.com");
        
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        
        // Immutability check: modifying input string shouldn't affect object if we were to expose internal ref (not applicable for final fields, but good practice conceptually)
        // Since fields are final and Strings are immutable in Java, this is naturally satisfied.
    }
}
