package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_profile_id_from_uuid() {
        var uuid = UUID.randomUUID();
        var id = new ProfileId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void should_reject_null_uuid() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }
    
    @Test
    void should_equality_based_on_value() {
        var uuid = UUID.randomUUID();
        var id1 = new ProfileId(uuid);
        var id2 = new ProfileId(uuid);
        
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
