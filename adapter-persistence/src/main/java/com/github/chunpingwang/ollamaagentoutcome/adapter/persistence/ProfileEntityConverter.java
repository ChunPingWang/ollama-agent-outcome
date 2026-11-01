package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain Profile 與 JPA Entity 之間的轉換邏輯。
 */
public class ProfileEntityConverter {

    /**
     * 將領域實體轉換為 JPA Entity。
     */
    public static ProfileJpaEntity toEntity(Profile profile) {
        return new ProfileJpaEntity(
                profile.getId().id().toString(),
                profile.getEmail().value(),
                profile.getDisplayName() != null ? profile.getDisplayName().value() : null
        );
    }

    /**
     * 將 JPA Entity 轉換為領域實體。
     */
    public static Profile toDomain(ProfileJpaEntity entity) {
        return new Profile(
                new ProfileId(java.util.UUID.fromString(entity.getId())),
                new Email(entity.getEmail()),
                entity.getDisplayName() != null && !entity.getDisplayName().isBlank() 
                        ? new DisplayName(entity.getDisplayName()) 
                        : new DisplayName((String) null)
        );
    }
}
