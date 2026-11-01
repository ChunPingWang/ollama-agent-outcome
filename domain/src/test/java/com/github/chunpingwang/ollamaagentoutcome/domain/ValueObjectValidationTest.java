package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectValidationTest {

    @Test
    void should_fail_to_create_profile_id_with_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void should_fail_to_create_email_with_blank() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_fail_to_create_display_name_if_length_exceeds_limit() {
        String longName = "a".repeat(256);
        assertThatThrownBy(() -> new DisplayName(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("display name length");
    }
}
