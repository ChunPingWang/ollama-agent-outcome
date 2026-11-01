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
@Import({JpaSaveProfilePort.class, JpaFindProfileByIdPort.class})
class JpaFindProfileByIdPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_find_profile_by_id() {
        UUID id = UUID.randomUUID();
        Email email = new Email("find@example.com");
        DisplayName displayName = new DisplayName("Finder");
        
        Profile profile = new Profile(id, email, displayName);
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear(); // 清除快取以確保從 DB 讀取

        Optional<Profile> foundOpt = findProfileByIdPort.findById(new ProfileId(id.toString()));
        
        assertThat(foundOpt).isPresent();
        Profile found = foundOpt.get();
        assertThat(found.id()).isEqualTo(new ProfileId(id.toString()));
        assertThat(found.email().address()).isEqualTo(email.address());
        assertThat(found.displayName().name()).isEqualTo(displayName.name());
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID nonExistentId = UUID.randomUUID();
        
        Optional<Profile> result = findProfileByIdPort.findById(new ProfileId(nonExistentId.toString()));
        
        assertThat(result).isEmpty();
    }
}
