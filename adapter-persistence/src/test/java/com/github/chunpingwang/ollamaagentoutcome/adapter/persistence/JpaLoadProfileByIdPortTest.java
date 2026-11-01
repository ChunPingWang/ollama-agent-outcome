package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfileByIdPort.class })
class JpaLoadProfileByIdPortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private LoadProfileByIdPort loadProfileByIdPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        String id = UUID.randomUUID().toString();
        Email email = new Email("load@example.com");
        DisplayName displayName = new DisplayName("Load User");
        
        Profile profile = Profile.create(new ProfileId(id), email, displayName);
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> loaded = loadProfileByIdPort.findById(id);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().id()).isEqualTo(new ProfileId(id));
        assertThat(loaded.get().email()).isEqualTo(email);
        assertThat(loaded.get().displayName()).isEqualTo(displayName);
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        Optional<Profile> result = loadProfileByIdPort.findById(UUID.randomUUID().toString());
        assertThat(result).isEmpty();
    }
}
