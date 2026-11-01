package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;






import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * JPA Load Profile Port 整合測試：驗證載入資料並正確轉換為 Domain Profile。
 */
// 每個測試後重建 context，避免交易與快取干擾
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
    void should_load_profile_by_id() {
        UUID id = UUID.randomUUID();
        String email = "load@example.com";
        String displayName = "Loader";

        // 先儲存一筆資料供查詢使用
        ProfileEntity entity = ProfileEntity.create(id, email, displayName);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> profile = loadProfilePort.findById(id);

        assertThat(profile).isPresent();
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile domainProfile = profile.get();
        assertThat(domainProfile.id().id()).isEqualTo(id);
        assertThat(domainProfile.email().value()).isEqualTo(email);
        assertThat(domainProfile.displayName().value()).isEqualTo(displayName);
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> result = loadProfilePort.findById(nonExistentId);

        assertThat(result).isEmpty();
    }
}
