package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 在 Domain Profile 與 JPA ProfileEntity 之間進行轉換。
 */
public class ProfileEntityConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     *
     * @param profile 領域實體
     * @return JPA Entity
     */
    public ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity(
                profile.id().value(),
                profile.name(),
                profile.email(),
                profile.note()
        );
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA Entity
     * @return 領域實體
     */
    public Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Profile(
                new ProfileId(entity.getId()),
                entity.getName(),
                entity.getEmail(),
                entity.getNote()
        );
    }

    /**
     * 將 JPA Entity ID 轉換為 Domain ProfileId。
     *
     * @param id UUID
     * @return 領域識別碼
     */
    public ProfileId toDomainId(UUID id) {
        return new ProfileId(id);
    }
}
