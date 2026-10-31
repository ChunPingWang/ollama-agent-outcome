package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaDeleteProfilePort.class)
@Transactional
class JpaDeleteProfilePortTest {

    @Autowired
    private DeleteProfilePort deleteProfilePort;

    @Autowired
    private ProfileJpaRepository repository;

    @Test
    void should_delete_profile_by_id() {
        // Given
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("delete@example.com");
        entity.setName("Delete User");
        entity.setAge(30);
        entity.setNew(true);
        repository.save(entity);
        repository.flush();

        // When
        deleteProfilePort.deleteById(id);

        // Then
        // 由於在同一交易內，且 deleteById 已呼叫 flush，需要再次 clear 快取以確認資料庫狀態
        repository.flush();
        assertThat(repository.findById(id)).isEmpty();
    }
}
