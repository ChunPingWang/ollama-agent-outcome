package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Converter between Domain Profile and JPA Entity.
 */
public final class ProfileConverter {

    private ProfileConverter() {}

    /**
     * Converts a domain Profile to a JPA Entity.
     * The resulting entity is marked as new (isNew = true).
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().value());
        entity.setEmail(profile.email().value());
        entity.setDisplayName(profile.displayName().value());
        // Ensure isNew is true for newly converted entities
        entity.isNew = true;
        return entity;
    }

    /**
     * Converts a JPA Entity back to a domain Profile.
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Profile(
                new ProfileId(entity.getId()),
                new Email(entity.getEmail()),
                new DisplayName(entity.getDisplayName())
        );
    }
}
