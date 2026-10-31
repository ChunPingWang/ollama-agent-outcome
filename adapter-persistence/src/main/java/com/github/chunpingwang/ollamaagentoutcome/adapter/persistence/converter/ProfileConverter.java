package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.converter;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import java.time.Instant;

/**
 * Domain Profile 與 JPA Entity 之間的轉換工具類別。
 */
public final class ProfileConverter {

    private ProfileConverter() {
        // 禁止實例化
    }

    /**
     * 將 JPA Entity 轉換為 Domain Model。
     *
     * @param entity JPA Entity
     * @return 對應的 Domain Profile
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Profile(
                new ProfileId(entity.getId()),
                new Name(entity.getName()),
                new Email(entity.getEmail()),
                entity.getCreatedAt()
        );
    }

    /**
     * 將 Domain Model 轉換為 JPA Entity。
     *
     * @param profile Domain Profile
     * @return 對應的 JPA Entity
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        // 直接使用全參數建構子建立，避免依賴 setter 與無參建構子的潛在編譯/執行期問題
        return new ProfileEntity(
                profile.getId().value(),
                profile.getName().text(),
                profile.getEmail().address(),
                profile.getCreatedAt()
        );
    }
}
