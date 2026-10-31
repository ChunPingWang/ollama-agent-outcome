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
@Import({ JpaSaveProfilePort.class, ProfileEntityConverter.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        var id = UUID.randomUUID();
        
        // 建立資料以確保刪除目標存在
        var saved = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id.toString()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("delete@example.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("To Be Deleted")
        ));
        
        // 清除快取，確保後續查詢從資料庫讀取
        entityManager.flush();
        entityManager.clear();

        // 執行刪除：使用 saved.id().value() 取得 UUID
        deleteProfilePort.deleteById(UUID.fromString(saved.id().value()));
        
        // 再次清除快取並驗證已不存在
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(UUID.fromString(saved.id().value()))).isEmpty();
    }
}
