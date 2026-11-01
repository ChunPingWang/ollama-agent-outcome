package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA ProfileEntity 之間的轉換器。
 */
public final class ProfileConverter {

    private ProfileConverter() {}

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     *
     * @param profile 領域物件
     * @return JPA Entity
     */
    public static ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
            profile.getId().getValue(),
            profile.getEmail().getAddress(),
            profile.getDisplayName().getName()
        );
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA Entity
     * @return 領域物件
     */
    public static Profile toDomain(ProfileEntity entity) {
        return new Profile(
            new ProfileId(entity.getId()),
            new Email(entity.getEmail()),
            new DisplayName(entity.getDisplayName())
        );
    }
}
