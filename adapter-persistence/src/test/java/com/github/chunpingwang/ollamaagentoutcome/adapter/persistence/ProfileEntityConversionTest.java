package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 驗證 ProfileConverter 的雙向轉換邏輯以及 Entity 的新增狀態標記行為。
 */
class ProfileEntityConversionTest {

    private final UUID testUuid = UUID.randomUUID();
    private final String testName = "Alice";
    private final String testEmail = "alice@example.com";

    @Test
    void should_convert_entity_to_domain_correctly() {
        // Given: JPA Entity with persisted state (isNew = false)
        var entity = new ProfileEntity(testUuid, testName, testEmail);
        entity.setNew(false); // Simulate post-load or manual persistence flag update

        // When: Convert to Domain
        var profile = ProfileConverter.toDomain(entity);

        // Then: Verify all fields map correctly
        assertThat(profile).isNotNull();
        assertThat(profile.id()).isEqualTo(new ProfileId(testUuid));
        assertThat(profile.name().value()).isEqualTo(testName);
        assertThat(profile.email().value()).isEqualTo(testEmail);
    }

    @Test
    void should_convert_domain_to_entity_correctly() {
        // Given: Domain Profile
        var domainProfile = Profile.create(
                new ProfileId(testUuid),
                new Name(testName),
                new Email(testEmail)
        );

        // When: Convert to Entity
        var entity = ProfileConverter.toEntity(domainProfile);

        // Then: Verify mapping and initial isNew status
        assertThat(entity.getId()).isEqualTo(testUuid);
        assertThat(entity.getName()).isEqualTo(testName);
        assertThat(entity.getEmail()).isEqualTo(testEmail);
        
        // Newly created entities must be marked as "new" for Persistable<UUID> logic
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_mark_new_entity_as_true_by_default() {
        // Given: A freshly instantiated JPA Entity without explicit ID assignment or load
        var entity = new ProfileEntity(null, testName, testEmail);

        // Then: It should be considered a new transient instance
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_mark_loaded_or_persisted_entity_as_false() {
        // Given: An entity that has been loaded from DB or explicitly set as persisted
        var entity = new ProfileEntity(testUuid, testName, testEmail);
        entity.setNew(false); 

        // Then: isNew() returns false
        assertThat(entity.isNew()).isFalse();
    }
}
