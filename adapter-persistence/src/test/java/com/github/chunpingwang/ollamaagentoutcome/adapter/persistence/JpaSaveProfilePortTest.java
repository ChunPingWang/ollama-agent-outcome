package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaSaveProfilePortTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaLoadProfilePort loadProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_new_profile() {
        // given
        String id = UUID.randomUUID().toString();
        Profile profile = createValidProfile(id);

        // when
        var saved = saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(new ProfileId(id));
        
        var found = entityManager.find(ProfileEntity.class, id);
        assertThat(found.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void should_update_existing_profile() {
        // given
        String id = UUID.randomUUID().toString();
        Profile initialProfile = createValidProfile(id);
        saveProfilePort.save(initialProfile);
        entityManager.flush();
        entityManager.clear();

        // when: load and update domain object, then save again
        var loaded = loadProfilePort.findById(id).orElseThrow();
        loaded.update(Email.of("newemail@test.com"), DisplayName.of("New Name"));
        saveProfilePort.save(loaded);
        entityManager.flush();
        entityManager.clear();

        // then
        var updated = entityManager.find(ProfileEntity.class, id);
        assertThat(updated.getEmail()).isEqualTo("newemail@test.com");
        assertThat(updated.getDisplayName()).isEqualTo("New Name");
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        // given
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        Profile profile1 = createValidProfileWithSameEmail(id1);
        Profile profile2 = createValidProfileWithSameEmail(id2);

        saveProfilePort.save(profile1);
        entityManager.flush();

        // when & then
        // 必須是該測試方法的最後一行，因為約束違反會中止交易
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate email address");
    }

    private Profile createValidProfile(String id) {
        return Profile.create(new ProfileId(id), Email.of("user@example.com"), DisplayName.of("John Doe"));
    }

    private Profile createValidProfileWithSameEmail(String id) {
        return Profile.create(new ProfileId(id), Email.of("duplicate@test.com"), DisplayName.of("Duplicate User"));
    }
}
