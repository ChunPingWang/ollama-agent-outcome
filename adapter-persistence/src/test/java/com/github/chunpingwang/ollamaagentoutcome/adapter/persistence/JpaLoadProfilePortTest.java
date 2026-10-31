package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// 每次 repository 呼叫各自交易：避免一級快取與交易中止問題；每個測試後重建 context 與 schema
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, ProfileEntityConverter.class, JpaLoadProfilePort.class })
class JpaLoadProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        var id = UUID.randomUUID();
        
        // 建立資料以確保查詢目標存在
        var saved = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id.toString()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("load@example.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Loader")
        ));
        
        // 清除快取，確保後續查詢從資料庫讀取
        entityManager.flush();
        entityManager.clear();

        // 執行查詢：使用 UUID (saved.id().value()) 而非 String
        var found = loadProfilePort.findById(UUID.fromString(saved.id().value()));

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id.toString()));
        assertThat(found.get().email()).isEqualTo(new com.github.chunpingwang.ollamaagentoutcome.domain.Email("load@example.com"));
        assertThat(found.get().displayName()).isEqualTo(new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Loader"));
    }

    @Test
    void should_return_empty_when_not_found() {
        var nonExistentId = UUID.randomUUID();
        
        var found = loadProfilePort.findById(nonExistentId);

        assertThat(found).isEmpty();
    }
}
