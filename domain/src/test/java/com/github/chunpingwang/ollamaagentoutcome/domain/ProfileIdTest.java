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
        String originalUuid = "550e8400-e29b-41d4-a716-446655440000";
        ProfileId id = new ProfileId(originalUuid);
        
        // 嘗試修改內部狀態（如果 value 是 final，這在編譯期就會阻止）
        // 這裡主要驗證回傳值與建構時一致且不可變
        assertThat(id.value()).isEqualTo(originalUuid);
    }
}
