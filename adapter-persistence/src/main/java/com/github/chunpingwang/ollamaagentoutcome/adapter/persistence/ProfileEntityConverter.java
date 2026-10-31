package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換器。
 */
class ProfileEntityConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    static ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
            profile.id().value(),
            profile.name().text(),
            profile.email().address()
        );
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    static Optional<Profile> toDomain(Optional<ProfileEntity> entityOpt) {
        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }
        ProfileEntity e = entityOpt.get();
        // 建立新的 Profile，isNew 設為 false（因為已存在於 DB）
        Profile p = new Profile(new ProfileId(e.getId()), new Name(e.getName()), new Email(e.getEmail()));
        return Optional.of(p);
    }
}
