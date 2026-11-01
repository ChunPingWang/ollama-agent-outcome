package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.Optional;
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
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaLoadProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        String id = UUID.randomUUID().toString();
        var profile = Profile.create(new ProfileId(id), new Email("load@test.com"), new DisplayName("Loader"));
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Profile> found = loadProfilePort.findById(id);
        
        assertThat(found).isPresent();
        assertThat(found.get().id().value()).isEqualTo(id);
        assertThat(found.get().email().address()).isEqualTo("load@test.com");
        assertThat(found.get().displayName().name()).isEqualTo("Loader");
    }

    @Test
    void should_return_empty_when_not_found() {
        String notFoundId = "non-existent-id";
        
        Optional<Profile> result = loadProfilePort.findById(notFoundId);
        
        assertThat(result).isEmpty();
    }
}
