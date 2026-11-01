package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
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

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaLoadProfilePortTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaLoadProfilePort loadProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        // given
        String id = UUID.randomUUID().toString();
        var profile = createValidProfile(id);
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> found = loadProfilePort.findById(id);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(new ProfileId(id));
        assertThat(found.get().email().getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_return_empty_when_not_found() {
        // given
        String nonExistentId = "non-existent-id";

        // when
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> found = loadProfilePort.findById(nonExistentId);

        // then
        assertThat(found).isEmpty();
    }

    private com.github.chunpingwang.ollamaagentoutcome.domain.Profile createValidProfile(String id) {
        return com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
            Email.of("user@example.com"),
            DisplayName.of("John Doe")
        );
    }
}
