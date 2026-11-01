package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

// 每次 repository 呼叫各自交易：避免一級快取與交易中止問題；每個測試後重建 context 與 schema
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Test
    void should_delete_profile_by_id() {
        String id = UUID.randomUUID().toString();
        var profile = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("delete@test.com"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Deleter")
        );
        
        saveProfilePort.save(profile);
        
        // Verify it exists first
        assertThat(loadProfilePort.findById(id)).isPresent();
        
        deleteProfilePort.deleteById(id);
        
        // After deletion, it should be gone
        assertThat(loadProfilePort.findById(id)).isEmpty();
    }
}
