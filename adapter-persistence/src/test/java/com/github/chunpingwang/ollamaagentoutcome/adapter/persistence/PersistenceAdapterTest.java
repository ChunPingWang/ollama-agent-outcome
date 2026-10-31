package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaFindProfileByIdPort.class, JpaDeleteProfilePort.class })
class PersistenceAdapterTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private FindProfileByIdPort findProfileByIdPort;

    @Autowired
    private DeleteProfilePort deleteProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_find_profile() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, "王小明", "xiaoming@example.com");

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> found = findProfileByIdPort.findById(id);
        assertThat(found).isPresent().get().satisfies(p -> {
            assertThat(p.getId()).isEqualTo(id);
            assertThat(p.getName()).isEqualTo("王小明");
            assertThat(p.getEmail()).isEqualTo("xiaoming@example.com");
        });
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        UUID id1 = UUID.randomUUID();
        String email = "unique@test.com";
        
        saveProfilePort.save(new Profile(id1, "User 1", email));
        entityManager.flush();
        entityManager.clear();

        UUID id2 = UUID.randomUUID();
        Profile duplicateEmailProfile = new Profile(id2, "User 2", email);

        // 唯一性測試必須是交易的最後一個動作，避免後續查詢因 transaction aborted 而失敗
        assertThatThrownBy(() -> saveProfilePort.save(duplicateEmailProfile))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email detected");
                
        // Verify original is still there and no partial state left
        entityManager.clear(); 
        assertThat(findProfileByIdPort.findById(id1)).isPresent();
        assertThat(findProfileByIdPort.findById(id2)).isEmpty();
    }

    @Test
    void should_find_empty_for_non_existent_id() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Profile> result = findProfileByIdPort.findById(nonExistentId);
        assertThat(result).isEmpty();
    }

    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, "To Delete", "delete@example.com");
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        assertThat(findProfileByIdPort.findById(id)).isEmpty();
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_profile() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> deleteProfilePort.deleteById(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found with ID: " + nonExistentId);
    }
}
