package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換器。
 */
public final class ProfileConverter {

    private ProfileConverter() {}

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.getId().getValue());
        entity.setEmail(profile.getEmail().getAddress());
        entity.setDisplayName(profile.getDisplayName());
        // When converting from domain, it's considered a new transient state 
        // unless explicitly managed by the repository save logic which handles isNew flag internally via @PostLoad/Persist.
        // However, for explicit creation flow:
        entity.isNew = true; 
        
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;        }
        
        ProfileId id = new ProfileId(entity.getId());
        Email email = new Email(entity.getEmail());
        DisplayName displayName = entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null;
        
        return Profile.create(id, email, displayName);
    }
}
