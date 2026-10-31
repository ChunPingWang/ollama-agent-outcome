package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain 層 Profile 與 Persistence 層 ProfileEntity 之間的轉換。
 */
class ProfileConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     *
     * @param profile 領域物件
     * @return JPA Entity
     */
    ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.getId().id());
        entity.setName(profile.getName());
        entity.setEmail(profile.getEmail().value());
        entity.setNew(true);
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA 實體
     * @return 領域物件
     */
    Profile toDomain(ProfileEntity entity) {
        ProfileId profileId = new ProfileId(entity.getId());
        Email email = new Email(entity.getEmail());
        return new Profile(profileId, entity.getName(), email);
    }
}
