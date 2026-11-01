package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;





import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * 測試 JpaLoadProfile Port 的查詢行為。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaLoadProfilePort.class)
class JpaLoadProfilePortTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_find_profile_by_id_successfully() {
        // Arrange
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity(id, "load@test.com", "Load User");
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        // Act
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> result = loadProfilePort.findById(id);

        // Assert
        assertThat(result).isPresent();
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile = result.get();
        assertThat(profile.id().getValue()).isEqualTo(id);
        assertThat(profile.email().getAddress()).isEqualTo("load@test.com");
        assertThat(profile.displayName().getName()).isEqualTo("Load User");
    }

    @Test
    void should_return_empty_when_not_found() {
        String nonExistentId = UUID.randomUUID().toString();

        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> result = loadProfilePort.findById(nonExistentId);

        assertThat(result).isEmpty();
    }
}
