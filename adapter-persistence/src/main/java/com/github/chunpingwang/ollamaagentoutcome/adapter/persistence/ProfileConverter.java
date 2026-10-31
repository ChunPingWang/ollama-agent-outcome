package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換器。
 */
public final class ProfileConverter {

    private ProfileConverter() {
    }

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    public static ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
                profile.id().id(),
                profile.name(),
                profile.email(),
                profile.note()
        );
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    public static Profile toDomain(ProfileEntity entity) {
        return Profile.create(
                new ProfileId(entity.getId()),
                entity.getName(),
                entity.getEmail(),
                entity.getNote()
        );
    }
}
