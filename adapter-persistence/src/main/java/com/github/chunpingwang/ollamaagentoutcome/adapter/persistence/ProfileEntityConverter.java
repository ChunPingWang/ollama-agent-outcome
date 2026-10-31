package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 在 Domain Profile 與 JPA ProfileEntity 之間進行轉換的靜態工具類別。
 */
final class ProfileEntityConverter {

    private ProfileEntityConverter() {
        // Utility class
    }

    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        var id = new ProfileId(entity.getId());
        var email = new Email(entity.getEmail());
        var displayName = new DisplayName(entity.getDisplayName());
        return new Profile(id, email, displayName);
    }

    static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        var entity = new ProfileEntity();
        entity.setId(profile.id().value());
        entity.setEmail(profile.email().value());
        entity.setDisplayName(profile.displayName() != null ? profile.displayName().value() : null);
        // The default constructor sets isNew=true, which is correct for a new domain object being persisted.
        // No need to manually call any method as the field is initialized in the constructor.
        return entity;
    }
}
