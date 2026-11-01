package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換器。
 */
@Component
public class ProfileEntityConverter {

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     */
    public ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        // 建立新實體時，isNew 預設為 true（在建構子中設定）
        ProfileEntity entity = new ProfileEntity(
                profile.getId().value(),
                profile.getEmailString(),
                profile.getDisplayNameString()
        );
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     */
    public Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 使用靜態工廠方法重建 Domain 物件
        return Profile.create(entity.getId(), entity.getEmail(), entity.getDisplayName());
    }
}
