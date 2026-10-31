package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Static utility class to convert between Domain Profile and JPA ProfileEntity.
 */
final class ProfileConverter {

    private ProfileConverter() {
        // Utility class
    }

    /**
     * Converts a JPA Entity to a Domain Profile.
     */
    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return Profile.create(
                new ProfileId(entity.getId()),
                new Name(entity.getName()),
                new Email(entity.getEmail())
        );
    }

    /**
     * Converts a Domain Profile to a JPA Entity.
     * The resulting entity is marked as isNew = true by default in the constructor,
     * but for explicit conversion from an existing domain object with an ID, 
     * we ensure it's treated correctly based on context or let the caller manage state.
     * However, typically when converting a managed domain object back to persistable form,
     * if it was just created via factory, it might be new. If loaded, it's not.
     * To keep this simple and aligned with Persistable logic:
     * When creating NEW profiles, we pass a profile that effectively represents a "new" instance.
     * But since Profile itself doesn't track persistence state, we rely on the fact that
     * `toEntity` creates a fresh entity which defaults to isNew=true unless specified otherwise?
     * Actually, looking at the test `should_convert_domain_to_entity_correctly`, it expects `isNew()` to be TRUE.
     * And `ProfileConverter.toEntity(domainProfile)` where domainProfile has an ID should still return an entity with isNew()=true?
     * Wait, if I load an entity, convert to domain, then save again (update), I need isNew=false.
     * Let's look at JpaSaveProfilePort implementation strategy later. Usually:
     * 1. New flow: Create Domain -> Convert to Entity (isNew=true) -> Save.
     * 2. Update flow: Load Entity -> Convert to Domain -> Modify Domain -> Convert to Entity?? 
     *    OR better: Load Entity -> Keep Entity -> Update fields directly in Adapter or use setter.
     *    
     * The prompt says: "更新流程：先 findById 取得受管 Entity，再更新欄位". This implies the Port might not always go through Converter for updates, 
     * OR the Converter handles both directions cleanly.
     * If `toEntity(profile)` is used for update, and profile came from DB, we want isNew=false.
     * But Profile doesn't know its origin.
     * Standard pattern: 
     * - For CREATE: `toEntity(newDomainObject)` returns new Entity with isNew=true.
     * - For UPDATE: Often done by loading the managed entity and updating it directly, avoiding conversion back to a detached state unless necessary.
     * However, the test `should_convert_domain_to_entity_correctly` creates a domain object via factory and expects isNew()=true.
     * So `toEntity` generally produces a NEW instance.
     * How do we handle updates then?
     * In JpaSaveProfilePort.save(Profile p):
     *   Option A: Check if ID exists in DB. If yes, load entity, update fields, save. (Doesn't use toEntity(p) for update).
     *   Option B: Use toEntity(p). If p was loaded previously, how does converter know? It doesn't.
     *   
     * Let's look at the test again: `should_mark_loaded_or_persisted_entity_as_false`. This tests setting setNew(false) manually or post-load.
     * The test `should_convert_domain_to_entity_correctly` checks that converting a freshly created profile results in an entity where isNew is true.
     * 
     * Implementation Strategy for Port:
     * When saving:
     * 1. Try to find existing entity by ID.
     * 2. If present -> Update its fields directly using setters on the managed Entity. Save it. (This avoids conversion issues for updates).
     * 3. If absent -> Convert Domain Profile to new Entity using `toEntity(profile)`. Since this is a "new" domain object being persisted, `isNew()` should be true.
     * 
     * Does `toEntity` need to handle the "update from loaded state" case?
     * Usually no, because we keep the entity managed. But if we MUST convert back:
     * We could add a flag to Profile? No, Domain must be pure.
     * So `ProfileConverter.toEntity(Profile)` will ALWAYS create a NEW instance with `isNew = true`.
     * For updates, the Adapter layer will load the entity and update it directly, NOT use `toEntity(domainProfile)`.
     * This satisfies the requirement "先 findById 取得受管 Entity，再更新欄位".
     */
    static ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
                profile.id().id(),
                profile.name().value(),
                profile.email().value()
        );
    }
}
