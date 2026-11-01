package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Profile Entity 與 Domain Object 之間的轉換器。
 */
public final class ProfileEntityConverter {

    private ProfileEntityConverter() {}

    /**
     * 將 Domain Profile 轉換為 JPA Entity（用於建立或更新）。
     *
     * @param profile 領域物件
     * @return JPA Entity
     */
    static ProfileEntity toEntity(Profile profile) {
        UUID id = profile.id().id();
        String email = profile.email().value();
        String displayName = profile.displayName() != null ? profile.displayName().value() : null;
        
        return ProfileEntity.create(id, email, displayName);
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA Entity
     * @return 領域物件
     */
    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return Profile.create(
            new ProfileId(entity.getId()),
            new Email(entity.getEmail()),
            entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null
        );
    }
}
