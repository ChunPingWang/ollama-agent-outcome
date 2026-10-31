package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile;

import java.time.Instant;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;

/**
 * Utility class for converting between Domain Profile and JPA Entity.
 */
public final class JpaProfileConverter {

    private JpaProfileConverter() {
        // Utility class
    }

    /**
     * Converts a Domain Profile to a JPA Entity.
     *
     * @param profile the domain profile
     * @return the corresponding JPA entity
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        String id = profile.getId().toString();
        Email emailVO = profile.getEmail();
        DisplayName displayNameVO = profile.getDisplayName();
        Bio bioVO = profile.getBio();
        
        Instant createdAt = profile.getCreatedAt();
        Instant updatedAt = profile.getUpdatedAt();

        ProfileEntity entity = new ProfileEntity(
            id,
            emailVO != null ? emailVO.address() : null,
            displayNameVO != null ? displayNameVO.name() : null,
            bioVO != null ? bioVO.content() : null,
            createdAt,
            updatedAt
        );
        
        // If it's already persisted, mark as not new
        if (!profile.isNew()) {
            entity.markNotNew();
        }
        
        return entity;
    }

    /**
     * Converts a JPA Entity to a Domain Profile.
     *
     * @param entity the jpa entity
     * @return the corresponding domain profile
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        UUID uuidId = UUID.fromString(entity.getId());
        Email email = entity.getEmail() != null ? Email.of(entity.getEmail()) : null;
        DisplayName displayName = entity.getDisplayName() != null ? DisplayName.of(entity.getDisplayName()) : null;
        Bio bio = entity.getBio() != null ? Bio.of(entity.getBio()) : null;
        
        Instant createdAt = entity.getCreatedAt();
        Instant updatedAt = entity.getUpdatedAt();

        Profile profile = new Profile(uuidId, email, displayName, bio);
        
        // Mark as not new since it came from DB
        profile.markNotNew();

        return profile;
    }
}
