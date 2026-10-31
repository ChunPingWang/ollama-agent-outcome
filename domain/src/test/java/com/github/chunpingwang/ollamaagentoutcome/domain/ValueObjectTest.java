package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void should_create_valid_profile_id() {
        String uuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void should_reject_empty_profile_id() {
        assertThatThrownBy(() -> new ProfileId(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為空");
    }

    @Test
    void should_reject_blank_profile_id() {
        // 修正：ProfileId 建構子使用 isBlank()，會同時涵蓋空白字串與純空格字串
        // 因此錯誤訊息統一為 "不能為空"，測試預期也應一致
        assertThatThrownBy(() -> new ProfileId("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為空");
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.address()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email("  "))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_create_valid_name() {
        Name name = new Name("王小明");
        assertThat(name.text()).isEqualTo("王小明");
    }

    @Test
    void should_reject_too_long_name() {
        String longName = "a".repeat(51);
        assertThatThrownBy(() -> new Name(longName))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("長度必須在 1 到 50 之間");
    }

    @Test
    void should_reject_empty_name() {
        assertThatThrownBy(() -> new Name(""))
                .isInstanceOf(InvalidNameException.class);
    }
}
