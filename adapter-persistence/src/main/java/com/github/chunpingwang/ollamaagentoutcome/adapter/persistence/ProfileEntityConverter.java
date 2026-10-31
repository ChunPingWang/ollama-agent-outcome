package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain Profile 與 JPA Entity 之間的轉換邏輯。
 */
@Component
class ProfileEntityConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    ProfileEntity toEntity(Profile profile) {
        var entity = new ProfileEntity();
        entity.setId(UUID.fromString(profile.id().value()));
        entity.setEmail(profile.email().address());
        entity.setDisplayName(profile.displayName().name());
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Profile(
                new ProfileId(entity.getId().toString()),
                new Email(entity.getEmail()),
                new DisplayName(entity.getDisplayName())
        );
    }
}
