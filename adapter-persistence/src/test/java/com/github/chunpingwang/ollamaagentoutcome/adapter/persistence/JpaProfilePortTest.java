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
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        JpaSaveProfilePort.class,
        JpaLoadProfilePort.class,
        JpaDeleteProfilePort.class,
        ProfileEntityConverter.class
})
class JpaProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Test
    void should_save_profile_successfully() {
        // Given
        ProfileId id = ProfileId.generate();
        Profile profile = new Profile(id, "王小明", "xiaoming@example.com", "測試備註");

        // When
        Profile saved = saveProfilePort.save(profile);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.name()).isEqualTo("王小明");
        assertThat(saved.email()).isEqualTo("xiaoming@example.com");
        assertThat(saved.note()).isEqualTo("測試備註");
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        ProfileId id1 = ProfileId.generate();
        Profile profile1 = new Profile(id1, "王小明", "unique@example.com", "備註1");
        saveProfilePort.save(profile1);

        ProfileId id2 = ProfileId.generate();
        Profile profile2 = new Profile(id2, "李大明", "unique@example.com", "重複備註");

        // When & Then
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void should_load_profile_by_id() {
        // Given
        ProfileId id = ProfileId.generate();
        Profile profile = new Profile(id, "王小明", "load@example.com", "查詢測試");
        saveProfilePort.save(profile);

        // When
        Optional<Profile> found = loadProfilePort.findById(id);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(found.get().name()).isEqualTo("王小明");
        assertThat(found.get().email()).isEqualTo("load@example.com");
    }

    @Test
    void should_return_empty_when_loading_non_existent_profile() {
        // Given
        ProfileId id = ProfileId.generate();

        // When
        Optional<Profile> found = loadProfilePort.findById(id);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void should_delete_profile_successfully() {
        // Given
        ProfileId id = ProfileId.generate();
        Profile profile = new Profile(id, "王小明", "delete@example.com", "刪除測試");
        saveProfilePort.save(profile);

        // When
        deleteProfilePort.deleteById(id);

        // Then
        Optional<Profile> found = loadProfilePort.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void should_throw_profile_not_found_exception_when_deleting_non_existent() {
        // Given
        ProfileId id = ProfileId.generate();

        // When & Then
        assertThatThrownBy(() -> deleteProfilePort.deleteById(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
