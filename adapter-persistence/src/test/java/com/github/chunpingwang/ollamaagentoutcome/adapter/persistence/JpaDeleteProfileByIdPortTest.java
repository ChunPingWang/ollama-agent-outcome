package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



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

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaFindProfileByIdPort.class, JpaDeleteProfileByIdPort.class})
class JpaDeleteProfileByIdPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private JpaDeleteProfileByIdPort deleteProfileByIdPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        Email email = new Email("delete@example.com");
        
        Profile profile = new Profile(id, email, null);
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // 驗證存在
        assertThat(findProfileByIdPort.findById(new ProfileId(id.toString()))).isPresent();

        // 執行刪除
        deleteProfileByIdPort.deleteById(new ProfileId(id.toString()));
        entityManager.flush();
        entityManager.clear();

        // 驗證已不存在
        Optional<Profile> result = findProfileByIdPort.findById(new ProfileId(id.toString()));
        assertThat(result).isEmpty();
    }

    @Test
    void should_handle_deleting_non_existent_profile_gracefully() {
        UUID nonExistentId = UUID.randomUUID();
        
        // 嘗試刪除不存在的 ID，不應拋出異常（或根據實作拋出特定例外，此處假設靜默處理或僅檢查 DB）
        // 為了測試穩定性，我們預期它不會因為 "not found" 而導致整個測試崩潰，通常 JPA deleteById 對不存在的主鍵只是影響行數為0
        deleteProfileByIdPort.deleteById(new ProfileId(nonExistentId.toString()));
        
        // 確認交易仍可繼續且資料狀態符合預期（即沒有被錯誤地創建或其他副作用）
        var count = entityManager.getEntityManager().createQuery("SELECT COUNT(e) FROM ProfileEntity e", Long.class).getSingleResult();
        assertThat(count).isEqualTo(0L);
    }
}
