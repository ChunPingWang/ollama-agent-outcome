package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain Profile 與 JPA ProfileEntity 之間的轉換。
 */
class ProfileConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().value());
        entity.setName(profile.name());
        entity.setEmail(profile.email().value());
        // 新建立的 Profile 轉換為 Entity 時，預設為 isNew = true
        entity.setNew(true);
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    Profile toDomain(ProfileEntity entity) {
        return new Profile(
                new ProfileId(entity.getId()),
                entity.getName(),
                new Email(entity.getEmail())
        );
    }
}
