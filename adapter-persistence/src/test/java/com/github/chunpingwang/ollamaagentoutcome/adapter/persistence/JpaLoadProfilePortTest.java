package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
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
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, ProfileEntityConverter.class })
class JpaLoadProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("load@example.com");
        var displayName = new DisplayName("Loader");
        var profile = new Profile(id, email, displayName);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> found = loadProfilePort.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(found.get().email()).isEqualTo(email);
        assertThat(found.get().displayName()).isEqualTo(displayName);
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        var nonExistentId = new ProfileId(UUID.randomUUID());

        Optional<Profile> found = loadProfilePort.findById(nonExistentId);

        assertThat(found).isEmpty();
    }
}
