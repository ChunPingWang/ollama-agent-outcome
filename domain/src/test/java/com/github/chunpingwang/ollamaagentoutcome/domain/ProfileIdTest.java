package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_with_valid_uuid_string() {
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
        ProfileId id1 = new ProfileId(originalUuid);
        
        // Verify immutability by checking that the internal state cannot be changed
        // Since it's a record, fields are final. We just verify equality and value access.
        assertThat(id1).extracting(ProfileId::value).isEqualTo(originalUuid);
        
        ProfileId id2 = new ProfileId(originalUuid);
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
