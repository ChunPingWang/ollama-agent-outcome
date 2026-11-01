package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileIdTest {

    private final UUID validUuid = UUID.randomUUID();

    @Test
    void should_create_with_valid_uuid() {
        ProfileId id = new ProfileId(validUuid);
        assertThat(id.id()).isEqualTo(validUuid);
    }

    @Test
    void should_reject_null_uuid() {
        assertThatThrownBy(() -> new ProfileId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id");
    }
    
    @Test
    void should_equality_check_based_on_id() {
        ProfileId i1 = new ProfileId(validUuid);
        ProfileId i2 = new ProfileId(validUuid);
        assertThat(i1).isEqualTo(i2);
        assertThat(i1.hashCode()).isEqualTo(i2.hashCode());
        
        ProfileId i3 = new ProfileId(UUID.randomUUID());
        assertThat(i1).isNotEqualTo(i3);
    }
}
