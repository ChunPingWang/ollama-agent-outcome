package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_profile_id_with_valid_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);

        assertThat(profileId.getValue()).isEqualTo(uuid);
    }

    @Test
    void should_throw_exception_when_uuid_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID cannot be null");
    }
}
