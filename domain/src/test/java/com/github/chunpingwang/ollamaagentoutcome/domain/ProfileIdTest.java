package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_profile_id_with_valid_uuid() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        ProfileId profileId = new ProfileId(uuid);

        assertThat(profileId.getValue()).isEqualTo(uuid);
    }

    @Test
    void should_throw_exception_when_id_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    void should_throw_exception_when_id_is_blank() {
        assertThatThrownBy(() -> new ProfileId("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be blank");
    }
}
