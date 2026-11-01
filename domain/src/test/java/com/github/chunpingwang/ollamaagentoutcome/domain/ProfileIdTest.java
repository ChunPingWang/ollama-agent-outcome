package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_with_valid_uuid_string() {
        String uuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void should_throw_when_null_value() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void should_throw_when_blank_value() {
        assertThatThrownBy(() -> new ProfileId(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void should_throw_when_invalid_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    void should_be_immutable() {
        // Records are immutable by default, but we verify the value doesn't change if accessed via getter
        String originalUuid = "550e8400-e29b-41d4-a716-446655440000";
        ProfileId id = new ProfileId(originalUuid);
        
        // Since it's a record with a final field (String), reassigning 'value' isn't possible directly.
        // We just assert that accessing it returns the same reference/value.
        assertThat(id.value()).isEqualTo(originalUuid);
    }
}
