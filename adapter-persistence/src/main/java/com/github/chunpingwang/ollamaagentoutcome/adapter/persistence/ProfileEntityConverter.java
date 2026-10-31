package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain Profile 與 JPA ProfileEntity 之間的雙向轉換。
 */
final class ProfileEntityConverter {

    private ProfileEntityConverter() {
    }

    /**
     * 將 Domain Profile 轉換為 JPA ProfileEntity。
     *
     * @param profile 領域物件
     * @return JPA Entity
     */
    static ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity(
                profile.getId().getValue(),
                profile.getEmail().getValue(),
                profile.getName().getValue(),
                profile.getAge().getValue()
        );
        return entity;
    }

    /**
     * 將 JPA ProfileEntity 轉換為 Domain Profile。
     *
     * @param entity JPA Entity
     * @return 領域物件
     */
    static Profile toDomain(ProfileEntity entity) {
        return new Profile(
                new ProfileId(entity.getId()),
                new Email(entity.getEmail()),
                new Name(entity.getName()),
                new Age(entity.getAge())
        );
    }
}
