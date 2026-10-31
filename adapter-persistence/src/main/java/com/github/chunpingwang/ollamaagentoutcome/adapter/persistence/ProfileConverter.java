package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain Profile 與 JPA Entity 之間的轉換邏輯。
 */
public final class ProfileConverter {

    private ProfileConverter() {}

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    public static ProfileEntity toEntity(Profile profile) {
        var entity = new ProfileEntity();
        entity.setId(profile.id().value());
        entity.setEmail(profile.email().address());
        entity.setDisplayName(profile.displayName().name());
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        return Profile.create(
                new ProfileId(entity.getId()),
                new Email(entity.getEmail()),
                new DisplayName(entity.getDisplayName())
        );
    }
}
