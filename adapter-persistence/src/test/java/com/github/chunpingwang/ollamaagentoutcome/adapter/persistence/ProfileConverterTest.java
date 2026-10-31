package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 ProfileConverter 在 Domain Profile 與 JPA ProfileEntity 之間的轉換邏輯。
 */
class ProfileConverterTest {

    private final ProfileConverter converter = new ProfileConverter();

    @Test
    void should_convert_domain_to_entity() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), "張三", new Email("zhangsan@example.com"));

        // When
        ProfileEntity entity = converter.toEntity(profile);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("張三");
        assertThat(entity.getEmail()).isEqualTo("zhangsan@example.com");
        assertThat(entity.isNew()).isTrue(); // 新建立的 Entity 應標記為 isNew
    }

    @Test
    void should_convert_entity_to_domain() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setName("李四");
        entity.setEmail("lisi@example.com");
        entity.setNew(false);

        // When
        Profile profile = converter.toDomain(entity);

        // Then
        assertThat(profile.id().value()).isEqualTo(id);
        assertThat(profile.name()).isEqualTo("李四");
        assertThat(profile.email().value()).isEqualTo("lisi@example.com");
    }

    @Test
    void should_preserve_is_new_flag_when_converting_to_entity() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), "王五", new Email("wangwu@example.com"));

        // When
        ProfileEntity entity = converter.toEntity(profile);

        // Then
        assertThat(entity.isNew()).isTrue();
    }
}
