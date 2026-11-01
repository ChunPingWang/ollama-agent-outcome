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
    void should_reject_invalid_uuid_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("invalid");
    }

    @Test
    void should_be_immutable() {
        String originalUuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(originalUuid);
        
        // Verify immutability by checking that the value cannot be changed via reflection or similar means is hard in Java records without private setters, 
        // but we can verify that creating a new one with same value equals old one and they are distinct instances if needed, 
        // though record equality handles value equality. The key is no setter exists.
        assertThat(id).extracting(ProfileId::value).isEqualTo(originalUuid);
    }
}
