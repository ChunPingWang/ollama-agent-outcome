package com.github.chunpingwang.ollamaagentoutcome.domain;





import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_with_valid_uuid() {
        String uuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.getValue()).isEqualTo(uuid.toLowerCase());
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
        
        // 由於 record 是 immutable，無法修改 value。
        // 這裡僅驗證取得值與建立時一致，且 record 的 equals/hashCode 行為正確。
        ProfileId another = new ProfileId(originalUuid);
        assertThat(id).isEqualTo(another);
        assertThat(id.hashCode()).isEqualTo(another.hashCode());
    }
}
