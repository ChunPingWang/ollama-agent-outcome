package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_generate_uuid_for_profile_id() {
        ProfileId id = ProfileId.generate();
        assertThat(id.value()).isNotNull();
        assertThat(id.value()).isInstanceOf(UUID.class);
    }

    @Test
    void should_equality_profile_id_by_value() {
        UUID uuid = UUID.randomUUID();
        ProfileId id1 = new ProfileId(uuid);
        ProfileId id2 = new ProfileId(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void should_create_profile_with_valid_data() {
        ProfileId id = ProfileId.generate();
        String name = "王小明";
        String email = "xiaoming@example.com";
        String note = "測試備註";

        Profile profile = new Profile(id, name, email, note);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.note()).isEqualTo(note);
    }

    @Test
    void should_create_profile_with_null_note() {
        ProfileId id = ProfileId.generate();
        String name = "王小明";
        String email = "xiaoming@example.com";

        Profile profile = new Profile(id, name, email, null);

        assertThat(profile.note()).isNull();
    }

    @Test
    void should_throw_when_name_is_blank() {
        ProfileId id = ProfileId.generate();
        String email = "xiaoming@example.com";

        assertThatThrownBy(() -> new Profile(id, "", email, "note"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");

        assertThatThrownBy(() -> new Profile(id, "   ", email, "note"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");
    }

    @Test
    void should_throw_when_email_is_invalid() {
        ProfileId id = ProfileId.generate();
        String name = "王小明";

        assertThatThrownBy(() -> new Profile(id, name, "invalid-email", "note"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");

        assertThatThrownBy(() -> new Profile(id, name, "@example.com", "note"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void should_be_immutable() {
        ProfileId id = ProfileId.generate();
        String name = "王小明";
        String email = "xiaoming@example.com";
        String note = "測試";

        Profile profile = new Profile(id, name, email, note);

        // 由於 Profile 是 record，欄位為 final，無法修改。
        // 此測試主要確保 getter 回傳的是原始值而非副本（若為 mutable 物件）。
        // 對於 String 和 UUID，它們本身是不可變的，所以直接比較即可。
        assertThat(profile.name()).isSameAs(name);
        assertThat(profile.email()).isSameAs(email);
        assertThat(profile.note()).isSameAs(note);
    }
}
