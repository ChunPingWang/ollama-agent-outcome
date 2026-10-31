package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_profile_id_with_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);

        assertThat(profileId.getValue()).isEqualTo(uuid);
    }

    @Test
    void should_not_allow_null_uuid() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID cannot be null");
    }

    @Test
    void should_be_equal_if_uuids_are_equal() {
        UUID uuid = UUID.randomUUID();
        ProfileId id1 = new ProfileId(uuid);
        ProfileId id2 = new ProfileId(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void should_not_be_equal_if_uuids_are_different() {
        ProfileId id1 = new ProfileId(UUID.randomUUID());
        ProfileId id2 = new ProfileId(UUID.randomUUID());

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void should_be_immutable() {
        UUID originalUuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(originalUuid);

        // Since UUID is immutable, we just verify the reference doesn't change
        // and the value remains the same.
        assertThat(profileId.getValue()).isSameAs(originalUuid);
    }
}
