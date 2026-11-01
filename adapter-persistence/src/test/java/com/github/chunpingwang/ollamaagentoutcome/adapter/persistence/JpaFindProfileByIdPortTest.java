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

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaFindProfileByIdPort.class, JpaSaveProfilePort.class, ProfileEntityConverter.class})
class JpaFindProfileByIdPortTest {

    @Autowired
    private FindProfileByIdPort findProfileByIdPort;

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_find_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        String emailStr = "find@example.com";
        String displayName = "Finder";
        
        Profile profile = Profile.create(id, emailStr, displayName);
        saveAndClear(profile);

        // When
        Optional<Profile> foundOpt = findProfileByIdPort.findById(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id));

        // Then
        assertThat(foundOpt).isPresent();
        Profile found = foundOpt.get();
        assertThat(found.getId().value()).isEqualTo(id);
        assertThat(found.getEmailString()).isEqualTo(emailStr.toLowerCase());
        assertThat(found.getDisplayNameString()).isEqualTo(displayName);
    }

    @Test
    void should_return_empty_when_not_found() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Profile> result = findProfileByIdPort.findById(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(nonExistentId));

        // Then
        assertThat(result).isEmpty();
    }

    private void saveAndClear(Profile profile) {
        var saved = saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
    }
}
