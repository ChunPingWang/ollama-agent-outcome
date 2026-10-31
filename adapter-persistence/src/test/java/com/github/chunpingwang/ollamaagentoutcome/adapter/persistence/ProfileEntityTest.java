package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * 測試 ProfileEntity 的 isNew 狀態管理與 JPA 生命週期回呼行為。
 * 注意：此測試需要 JPA Provider (Hibernate) 來觸發 @PostLoad/@PostPersist，
 * 因此通常需要在有 JPA 環境下執行，或模擬 EntityManager 行為。
 * 在此 TDD RED 階段，我們先定義測試結構，預期因類別不存在而編譯失敗。
 */
class ProfileEntityTest {

    @Test
    void should_be_new_when_created_manually() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("test@example.com");
        entity.setName("Test");
        entity.setAge(25);

        // When & Then
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_not_be_new_after_persist() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("test@example.com");
        entity.setName("Test");
        entity.setAge(25);

        // Simulate persist callback
        entity.onPersist();

        // When & Then
        assertThat(entity.isNew()).isFalse();
    }

    @Test
    void should_not_be_new_after_load() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("test@example.com");
        entity.setName("Test");
        entity.setAge(25);
        entity.setNew(true); // Simulate initial state

        // Simulate load callback
        entity.onLoad();

        // When & Then
        assertThat(entity.isNew()).isFalse();
    }
}
