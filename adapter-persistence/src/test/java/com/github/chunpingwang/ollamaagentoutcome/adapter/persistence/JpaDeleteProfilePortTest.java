package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaDeleteProfilePort.class })
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        String id = UUID.randomUUID().toString();
        var profile = Profile.create(new ProfileId(id), new Email("delete@test.com"), new DisplayName("Deleter"));
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
        
        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();
        
        deleteProfilePort.deleteById(id);
        entityManager.flush();
        entityManager.clear();
        
        // 刪除後應找不到該筆資料
        assertThat(entityManager.find(ProfileEntity.class, id)).isNull();
    }
}
