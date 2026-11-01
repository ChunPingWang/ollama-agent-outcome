package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Object 與 JPA Entity 之間的轉換器。
 */
public final class ProfileEntityConverter {

    private ProfileEntityConverter() {
        // Utility class
    }

    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) return null;
        var email = new Email(entity.getEmail());
        var displayName = entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null;
        return new Profile(new ProfileId(entity.getId()), email, displayName);
    }

    static ProfileEntity toEntity(com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile) {
        if (profile == null) return null;
        String displayNameValue = profile.displayName() != null ? profile.displayName().value() : null;
        return new ProfileEntity(profile.id().id(), profile.email().value(), displayNameValue);
    }
}
