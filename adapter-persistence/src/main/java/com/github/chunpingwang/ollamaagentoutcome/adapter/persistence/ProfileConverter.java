package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換工具類別。
 */
public final class ProfileConverter {

    private ProfileConverter() {}

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().getValue());
        entity.setEmail(profile.email().getAddress());
        entity.setDisplayName(profile.displayName().getName());
        // 預設視為新增，Port 層會根據資料庫存在性調整 isNew
        entity.setNew(true);
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Email email = new Email(entity.getEmail());
        DisplayName displayName = new DisplayName(entity.getDisplayName());
        ProfileId id = new ProfileId(entity.getId());
        
        return Profile.create(id, email, displayName);
    }
}
