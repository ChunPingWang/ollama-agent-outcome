package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.PersistenceException;

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
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository repository;

    @Test
    void should_save_and_find_profile() {
        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String displayName = "John Doe";

        ProfileEntity entity = ProfileEntity.create(id, email, displayName);
        repository.saveAndFlush(entity);

        Optional<ProfileEntity> found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
        assertThat(found.get().getDisplayName()).isEqualTo(displayName);
    }

    @Test
    void should_reject_duplicate_email() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String duplicateEmail = "dup@example.com";

        // 建立第一筆資料
        ProfileEntity first = ProfileEntity.create(id1, duplicateEmail, "First");
        repository.saveAndFlush(first);

        // 嘗試儲存第二筆相同 email，應拋出 DataIntegrityViolationException
        ProfileEntity second = ProfileEntity.create(id2, duplicateEmail, "Second");
        
        // 唯一約束違反測試：assertThatThrownBy 必須是該方法的最後一行
        assertThatThrownBy(() -> repository.saveAndFlush(second))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
