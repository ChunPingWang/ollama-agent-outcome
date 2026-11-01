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
 * JPA Delete Profile Port 整合測試：驗證刪除操作。
 */
// 每個測試後重建 context，避免交易與快取干擾
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaDeleteProfilePort.class, JpaLoadProfilePort.class})
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        String email = "delete@example.com";
        String displayName = "To Be Deleted";

        // 先建立一筆資料
        ProfileEntity entity = ProfileEntity.create(id, email, displayName);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        // 驗證存在
        assertThat(loadProfilePort.findById(id)).isPresent();

        // 執行刪除
        deleteProfilePort.deleteById(id);
        
        // 清除快取並重新查詢以確認刪除生效
        entityManager.flush();
        entityManager.clear();
        
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> result = loadProfilePort.findById(id);
        assertThat(result).isEmpty();
    }
}
