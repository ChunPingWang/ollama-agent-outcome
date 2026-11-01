package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




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

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaFindProfileByIdPort.class, JpaDeleteProfileByIdPort.class, ProfileEntityConverter.class})
class JpaDeleteProfileByIdPortTest {

    @Autowired
    private DeleteProfileByIdPort deleteProfileByIdPort;

    @Autowired
    private FindProfileByIdPort findProfileByIdPort;

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        String emailStr = "delete@example.com";
        
        Profile profile = Profile.create(id, emailStr, "To Be Deleted");
        saveAndClear(profile);

        // Verify it exists first
        assertThat(findProfileByIdPort.findById(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id))).isPresent();

        // When
        deleteProfileByIdPort.deleteById(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id));
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Profile> result = findProfileByIdPort.findById(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id));
        assertThat(result).isEmpty();
    }

    private void saveAndClear(Profile profile) {
        var saved = saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
    }
}
