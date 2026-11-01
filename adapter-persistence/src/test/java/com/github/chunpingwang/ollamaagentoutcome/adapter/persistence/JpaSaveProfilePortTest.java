package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 JpaSaveProfile Port 的儲存行為。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaSaveProfilePort.class)
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_save_new_profile_successfully() {
        String id = UUID.randomUUID().toString();
        Profile profile = createProfile(id, "save@test.com", "Save User");

        // Act: Save the domain object via port
        saveProfilePort.save(profile);
        
        // Flush and clear to ensure persistence in test transaction context
        entityManager.flush();
        entityManager.clear();

        // Verify it exists in DB
        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();
    }

    @Test
    void should_update_existing_profile_successfully() {
        // Arrange: Create initial record directly in entity manager to simulate existing data
        String id = UUID.randomUUID().toString();
        ProfileEntity existingEntity = new ProfileEntity(id, "update@test.com", "Old Name");
        entityManager.persistAndFlush(existingEntity);
        entityManager.clear();

        // Load as Domain Object
        Profile loadedProfile = ProfileConverter.toDomain(
            entityManager.find(ProfileEntity.class, id)
        );
        
        // Update Domain State
        Email updatedEmail = new Email("updated@test.com");
        DisplayName updatedName = new DisplayName("New Name");
        loadedProfile.update(updatedEmail, updatedName);

        // Act: Save back via Port (should update because isNew is false after load/persist cycle logic handled by converter or state)
        // Note: Our Converter sets isNew=true. For updates, we need to preserve the ID but let JPA know it's an update.
        // The Persistable interface relies on `isNew`. 
        // If we just call save with a new Entity from domain that has isNew=true, it might try INSERT again causing duplicate key if ID exists?
        // No, Spring Data checks existence of ID first for managed entities, but here we are passing a detached/new instance.
        // Actually, if `isNew` returns true, Spring Data tries INSERT. If ID already exists in DB, it fails.
        // So for UPDATE scenario using this port implementation which always creates NEW entity from domain:
        // We must ensure that when updating, we don't treat it as "new" if it already exists, OR rely on merge behavior.
        // However, standard JPA `saveAndFlush` on a new instance with existing ID throws exception unless configured otherwise.
        
        // Correction based on Plan 001 & System Prompt:
        // "更新流程：先 findById 取得受管 Entity，再更新欄位，不要以新 Entity 覆蓋。"
        // This implies the Port should handle finding and merging/updating properly or the Service layer does it.
        // But SaveProfilePort is simple `void save(Profile)`.
        // To support update correctly without explicit find-in-port (which LoadPort does), 
        // usually we check if ID exists. Or, better yet, since ProfileConverter sets isNew=true, 
        // we need to change logic: if ID exists in DB, set isNew=false before saving? 
        // Or simply use `merge` semantics. 
        
        // Let's stick to the simplest interpretation of `save`: Insert if new, Update if exists.
        // With Persistable<String>, if isNew() is true, Spring Data executes INSERT.
        // If we want UPDATE, isNew() MUST be false.
        // So, when converting Domain -> Entity for an EXISTING profile, we must ensure isNew is false.
        // How do we know it's existing? The Application Layer knows because it loaded it first.
        // BUT, our Converter doesn't have context.
        
        // Alternative approach often used: Check existence in Repository/Adapter before deciding insert/update.
        // Or, simpler: Just let JPA handle it via merge if we don't strictly enforce "new" flag for updates in this specific port design?
        // No, System Prompt says: "Domain 若自行產生識別碼... JPA Entity 不可同時使用 @GeneratedValue... 讓 Entity 實作 Persistable".
        // And "更新流程：先 findById 取得受管 Entity，再更新欄位". This suggests the Service might call Load then Save.
        // But SaveProfilePort takes a Profile object.
        
        // Let's assume the standard behavior: 
        // If I pass a Profile with ID that already exists, and my converter sets isNew=true, it will fail on INSERT duplicate key.
        // To fix this properly without changing domain structure too much:
        // In `JpaSaveProfilePort.save`, check if entity with ID exists. If yes, set isNew=false (or use merge).
        // However, checking existence adds overhead. 
        
        // Refined Strategy based on common Hexagonal patterns with JPA:
        // The Port implementation should probably just save. If it's an update scenario from App Layer, 
        // the App Layer usually loads the entity, modifies it, and saves. 
        // BUT our Domain Object is detached when passed to Port.
        
        // Let's implement a simple check in JpaSaveProfilePort or adjust Converter logic?
        // Actually, simplest way for `save` port to handle both create/update:
        // Check if ID exists in DB. If so, fetch existing managed entity, copy properties, flush.
        // If not, insert new.
        
        // For now, let's write the test expecting success. We need to make sure the code supports it.
        // I will modify JpaSaveProfilePort slightly to handle updates by checking existence if needed, 
        // OR assume that for Update UseCase, we might need a different approach? No, spec says SaveProfilePort.
        
        // Let's try this: In JpaSaveProfilePort, if isNew() would be true but ID exists, treat as update.
        // Or simpler: Just use repository.save(). But System Prompt insists on saveAndFlush for constraints.
        
        // Okay, let's look at ProfileConverter again. It sets isNew=true always.
        // This is problematic for Updates unless we change it.
        // Change: ProfileConverter.toEntity should NOT set isNew=true blindly. 
        // But Domain doesn't know persistence state.
        
        // Solution: The Port implementation handles the "Is New" determination based on DB presence.
        
        // Re-writing JpaSaveProfilePort logic mentally:
        /*
         public void save(Profile profile) {
             String id = profile.id().getValue();
             boolean exists = profileRepository.existsById(id);
             ProfileEntity entity = ProfileConverter.toEntity(profile);
             if (exists) {
                 entity.setNew(false); // Force update behavior
             } else {
                 entity.setNew(true);  // Insert behavior
             }
             profileRepository.saveAndFlush(entity);
         }
        */
        // This works and satisfies both Create and Update scenarios with a single port method.

        // Back to Test:
        entityManager.flush();
        entityManager.clear();

        // Act: Save updated domain object
        saveProfilePort.save(loadedProfile);
        
        entityManager.flush();
        entityManager.clear();

        // Verify update
        ProfileEntity updatedEntity = entityManager.find(ProfileEntity.class, id);
        assertThat(updatedEntity.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedEntity.getDisplayName()).isEqualTo("New Name");
    }

    @Test
    void should_throw_duplicate_email_exception_on_conflict() {
        // Arrange: Create first user
        String id1 = UUID.randomUUID().toString();
        Profile profile1 = createProfile(id1, "conflict@test.com", "User One");
        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        // Create second user with same email but different ID
        String id2 = UUID.randomUUID().toString();
        Profile profile2 = createProfile(id2, "conflict@test.com", "User Two");

        // Assert: Must be the last line in this test due to transaction abortion on constraint violation
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        // Note: We cannot verify state of DB here because transaction is aborted.
    }

    private Profile createProfile(String id, String email, String displayName) {
        return Profile.create(
            new ProfileId(id),
            new Email(email),
            new DisplayName(displayName)
        );
    }
}
