package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換器。
 */
public class ProfileEntityConverter {

    private ProfileEntityConverter() {}

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     *
     * @param profile 領域實體
     * @return JPA 實體
     */
    static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        UUID id = profile.getIdAsUUID();
        
        ProfileEntity entity = new ProfileEntity(id);
        entity.setEmail(profile.email().address());
        entity.setDisplayName(profile.displayName() != null ? profile.displayName().name() : null);
        
        // 若 ID 已存在於資料庫中（例如更新場景），isNew 應設為 false
        // 但在 Port 層面，我們通常先檢查是否存在來判斷是 INSERT 還是 UPDATE
        // 這裡僅做基本映射，isNew 狀態由 JpaSaveProfilePort 根據查詢結果設定
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA 實體
     * @return 領域實體
     */
    static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Email email = new Email(entity.getEmail());
        DisplayName displayName = entity.getDisplayName() != null && !entity.getDisplayName().isBlank() 
                ? new DisplayName(entity.getDisplayName()) 
                : null;
                
        return new Profile(entity.getId(), email, displayName);
    }
}
