package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_with_valid_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);

        assertThat(id.getValue()).isEqualTo(uuid);
    }

    @Test
    void should_throw_when_uuid_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID cannot be null");
    }

    @Test
    void should_be_equal_for_same_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId id1 = new ProfileId(uuid);
        ProfileId id2 = new ProfileId(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
