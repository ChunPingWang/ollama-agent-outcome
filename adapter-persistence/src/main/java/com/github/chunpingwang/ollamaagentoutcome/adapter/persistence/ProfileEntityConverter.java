package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Converter between Domain Profile and JPA Entity.
 */
public class ProfileEntityConverter {

    /**
     * Convert domain profile to JPA entity for saving.
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().id());
        entity.setEmail(profile.email().address());
        entity.setDisplayName(profile.displayName() != null ? profile.displayName().name() : null);
        // Always treat as new when converting from domain, isNew flag handled by Persistable interface logic or DB constraint
        // However, since we use saveAndFlush on existing entities in update flow, 
        // the converter is primarily used for Create. For Update, we load first then modify fields directly on managed entity.
        // So here we just set values. The 'isNew' state depends on whether it was loaded or newly created.
        // To be safe with Persistable<String>, we assume true for fresh conversions unless specified otherwise?
        // Actually, standard practice: New objects are isNew=true. Loaded objects have isNew=false via @PostLoad.
        // Since this method creates a NEW object instance, it should be considered "new" initially if not persisted yet.
        // But wait, if we pass an already persisted domain object to convert and save again?
        // In our architecture:
        // 1. Create: Domain.new -> Entity(new) -> Save. isNew=true.
        // 2. Read: Load -> ManagedEntity(isNew=false).
        // 3. Update: Load ManagedEntity -> Modify Fields -> Flush/Save. No conversion needed usually, but if we do:
        //    We shouldn't create a new Entity from Domain for updates if we want to preserve identity/versioning correctly without merge issues.
        //    However, JPA `save` (which calls merge in some contexts or insert/update based on id) works best if we know the state.
        //    Let's stick to: This converter is used by Port implementations. 
        //    For SaveProfilePort.save(profile): If profile is brand new, entity.isNew = true.
        
        // To simplify: The Port implementation will handle setting isNew appropriately or rely on ID existence check logic if Persistable wasn't used.
        // With Persistable<String>, isNew() determines INSERT vs UPDATE.
        // A freshly converted entity has never been loaded, so it IS NEW.
        entity.setIsNew(true); 
        
        return entity;
    }

    /**
     * Convert managed JPA entity back to domain model.
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        Email email = new Email(entity.getEmail());
        DisplayName displayName = entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null;
        ProfileId id = new ProfileId(entity.getId());
        return Profile.create(id, email, displayName);
    }
}
