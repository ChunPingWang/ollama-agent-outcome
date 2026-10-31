package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * 驗證 Value Objects (ProfileId, Email, DisplayName) 的不變條件與格式檢查。
 */
class ValueObjectValidationTest {

    @Test
    void should_create_profile_id_with_valid_uuid() {
        String validUuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(validUuid);
        assertThat(id.value()).isEqualTo(validUuid);
    }

    @Test
    void should_reject_invalid_uuid_format_for_profile_id() {
        assertThatThrownBy(() -> new ProfileId("not-a-valid-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void should_create_email_with_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.address()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailFormatException.class);
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailFormatException.class);
                
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_reject_malformed_email_address() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailFormatException.class);
                
        assertThatThrownBy(() -> new Email("@example.com"))
                .isInstanceOf(InvalidEmailFormatException.class);
                
        assertThatThrownBy(() -> new Email("user@"))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_trim_display_name_on_creation() {
        DisplayName name = new DisplayName("  Test User  ");
        assertThat(name.name()).isEqualTo("Test User");
    }

    @Test
    void should_handle_null_or_empty_display_name_as_empty_string() {
        DisplayName nullName = new DisplayName((String) null);
        assertThat(nullName.name()).isEmpty();
        
        DisplayName emptyName = new DisplayName("");
        assertThat(emptyName.name()).isEmpty();
        
        DisplayName blankName = new DisplayName("   ");
        assertThat(blankName.name()).isEmpty();
    }
}
