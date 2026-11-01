package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

// 每次 repository 呼叫各自交易：避免一級快取與交易中止問題；每個測試後重建 context 與 schema
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired
    private ProfileRepository repository;

    @Test
    void should_save_and_load_profile() {
        var entity = new ProfileEntity("550e8400-e29b-41d4-a716-446655440000", "test@example.com", "Tester");
        
        assertThat(entity.isNew()).isTrue();
        
        repository.saveAndFlush(entity);
        
        // 模擬 PostLoad 觸發，isNew 應變為 false
        assertThat(entity.isNew()).isFalse();
        
        var found = repository.findById("550e8400-e29b-41d4-a716-446655440000");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getDisplayName()).isEqualTo("Tester");
    }

    @Test
    void should_reject_duplicate_email() {
        var first = new ProfileEntity("id-1", "dup@test.com", "User 1");
        repository.saveAndFlush(first);
        
        var second = new ProfileEntity("id-2", "dup@test.com", "User 2");
        
        // DataIntegrityViolationException 是 JPA/DB 層拋出的原始例外
        assertThatThrownBy(() -> repository.saveAndFlush(second))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
