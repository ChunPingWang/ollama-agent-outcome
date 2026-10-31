package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

public final class ProfileConverter {

    private ProfileConverter() {}

    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) return null;
        
        String displayNameValue = entity.getDisplayName();
        
        // Use the static factory method as constructor is private
        return Profile.create(
            new ProfileId(entity.getId()),
            entity.getEmail(),
            displayNameValue
        );
    }

    static ProfileEntity toEntity(Profile profile) {
        if (profile == null) return null;
        
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.getId().value);
        entity.setEmail(profile.getEmail().address);
        
        // Handle potentially null display name safely
        String displayNameStr = profile.getDisplayName() != null ? profile.getDisplayName().name : null;
        entity.setDisplayName(displayNameStr);
        
        return entity;
    }
}
