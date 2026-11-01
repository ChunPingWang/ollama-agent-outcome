package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_profile_id_from_uuid() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        ProfileId id = new ProfileId(uuid);

        // then
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void should_equality_based_on_value() {
        // given
        UUID uuid = UUID.randomUUID();
        ProfileId id1 = new ProfileId(uuid);
        ProfileId id2 = new ProfileId(uuid);

        // then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void should_not_be_equal_to_different_values() {
        // given
        ProfileId id1 = new ProfileId(UUID.randomUUID());
        ProfileId id2 = new ProfileId(UUID.randomUUID());

        // then
        assertThat(id1).isNotEqualTo(id2);
    }
}
