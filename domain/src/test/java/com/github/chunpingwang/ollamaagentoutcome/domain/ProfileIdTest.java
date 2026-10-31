package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_profile_id_with_valid_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("should reject null UUID")
    void should_reject_null_uuid() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }
}
