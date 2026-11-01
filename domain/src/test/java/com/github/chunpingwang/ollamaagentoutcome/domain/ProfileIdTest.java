package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_valid_profile_id() {
        String uuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> new ProfileId(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
        
        assertThatThrownBy(() -> new ProfileId("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }
}
