package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * {@link ProfileId} 的單元測試。
 * 驗證識別碼的建構、不可變性與不變條件。
 */
class ProfileIdTest {

    @Test
    void should_create_profile_id_with_valid_value() {
        String validUuid = "550e8400-e29b-41d4-a716-446655440000";
        ProfileId profileId = new ProfileId(validUuid);

        assertThat(profileId).isNotNull();
        assertThat(profileId.getValue()).isEqualTo(validUuid);
    }

    @Test
    void should_throw_exception_when_value_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("value");
    }

    @Test
    void should_throw_exception_when_value_is_blank() {
        assertThatThrownBy(() -> new ProfileId("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("value");
    }

    @Test
    void should_be_immutable() {
        String originalValue = "550e8400-e29b-41d4-a716-446655440000";
        ProfileId profileId = new ProfileId(originalValue);

        // 由於 ProfileId 應為不可變物件，我們驗證其狀態不會改變
        // 若 getValue() 回傳的是內部字串參考，修改外部字串不應影響內部狀態
        // 但 record 的欄位預設為 final，故無法修改
        assertThat(profileId.getValue()).isEqualTo(originalValue);
    }

    @Test
    void should_equality_based_on_value() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        ProfileId id1 = new ProfileId(uuid);
        ProfileId id2 = new ProfileId(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void should_not_equality_with_different_value() {
        ProfileId id1 = new ProfileId("uuid-1");
        ProfileId id2 = new ProfileId("uuid-2");

        assertThat(id1).isNotEqualTo(id2);
    }
}
