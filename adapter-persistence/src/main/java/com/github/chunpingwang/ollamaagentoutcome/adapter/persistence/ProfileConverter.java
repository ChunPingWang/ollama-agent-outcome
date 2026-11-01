package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 負責 Domain Profile 與 JPA Entity 之間的雙向轉換。
 */
public class ProfileConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    public ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(profile.id(), profile.email().address(), profile.displayName().name());
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    public Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("實體不能為空");
        }
        return new Profile(
                entity.getId(),
                new Email(entity.getEmail()),
                new DisplayName(entity.getDisplayName())
        );
    }
}
