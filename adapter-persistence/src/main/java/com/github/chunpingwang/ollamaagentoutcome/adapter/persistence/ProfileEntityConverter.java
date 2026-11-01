package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Static utility class to convert between Domain Model and JPA Entity.
 */
public final class ProfileEntityConverter {

    private ProfileEntityConverter() {
        // Utility class
    }

    /**
     * Convert Domain Profile to JPA Entity.
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        boolean isNew = true;
        // If the entity already has an ID in DB context, it's not new anymore after load/save cycle handled by Port
        // However, for initial creation from domain object without prior persistence state, we assume new=true initially? 
        // Actually, Persistable.isNew is managed internally by Spring Data or manually set.
        // When converting a fresh domain object created via factory, it should be treated as new unless specified otherwise.
        // But wait, saveAndFlush handles insert vs update based on isNew().
        // We need to ensure that when we fetch from DB and modify, isNew becomes false.
        // Here, we are creating an entity FROM A DOMAIN OBJECT.
        // If this domain object was just loaded, its corresponding entity would have isNew=false.
        // Since we don't pass the old entity here directly but reconstruct, we rely on the fact that:
        // 1. New profiles come with no existing entity -> isNew=true.
        // 2. Existing profiles are loaded into Domain, then converted back. The converter doesn't know if it's "new" or "existing" purely from Domain Object alone if we lose state.
        // BUT: ProfileEntityConverter.toEntity(Profile) is called inside JpaSaveProfilePort.save().
        // In JpaSaveProfilePort.save(), we first check if exists. If yes, load -> convertToDomain -> update -> convertToEntity (which needs to preserve isNew=false).
        // So, simply calling `createNew` in converter for all cases might break updates because it sets isNew=true.
        
        // Correction: The Port implementation should handle the isNew flag logic properly by either passing a flag or managing the entity lifecycle carefully.
        // Let's look at how Spring Data handles Persistable. 
        // If we create a new Entity instance via constructor/new, isNew() returns true.
        // When we load an entity from DB, @PostLoad sets isNew = false.
        // So, when updating: Load Entity (isNew=false) -> Convert to Domain -> Update Domain -> Convert Back to Entity?
        // No! We shouldn't discard the managed entity. We should modify the existing managed entity OR merge.
        // However, standard pattern with Persistable often involves:
        // 1. Check existence.
        // 2. If not exist, create NEW Entity (isNew=true), save.
        // 3. If exist, LOAD Entity (managed, isNew=false), UPDATE its fields directly on the ENTITY object, then flush/save.
        
        // Therefore, ProfileEntityConverter.toEntity(Profile) is primarily used for CREATING new entities from domain objects that don't have a corresponding JPA entity yet.
        // For updates, we will update the loaded entity directly in the Port layer to preserve state and isNew status.
        
        return ProfileEntity.createNew(
            profile.id().value(),
            profile.email().getAddress(),
            profile.displayName().getName()
        );
    }

    /**
     * Convert JPA Entity to Domain Profile.
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return Profile.create(
            new ProfileId(entity.getId()),
            Email.of(entity.getEmail()),
            DisplayName.of(entity.getDisplayName())
        );
    }
}
