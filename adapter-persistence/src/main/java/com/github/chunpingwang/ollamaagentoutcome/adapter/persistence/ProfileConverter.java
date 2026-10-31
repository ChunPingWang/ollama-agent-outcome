package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain Profile 與 JPA ProfileEntity 之間的轉換。
 */
final class ProfileConverter {

    private ProfileConverter() {
        // 工具類別，禁止實例化
    }

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     *
     * @param profile 領域實體
     * @return JPA Entity
     */
    static ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().value());
        entity.setEmail(profile.email().value());
        entity.setName(profile.name().value());
        entity.setBio(profile.bio().value());
        entity.setNew(true);
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA Entity
     * @return 領域實體
     */
    static Profile toDomain(ProfileEntity entity) {
        return new Profile(
            new ProfileId(entity.getId()),
            new Email(entity.getEmail()),
            new Name(entity.getName()),
            new Bio(entity.getBio())
        );
    }
}
