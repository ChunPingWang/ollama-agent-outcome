package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 負責 Domain 物件與 JPA Entity 之間的轉換。
 */
public final class ProfileConverter {

    private ProfileConverter() {
        // 工具類別，禁止實例化
    }

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     *
     * @param profile 個人資料 Domain 物件
     * @return 轉換後的 JPA Entity
     */
    public static ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().getValue());
        entity.setEmail(profile.email().getValue());
        entity.setName(profile.name().getValue());
        entity.setAge(profile.age().getValue());
        entity.setNew(true);
        return entity;
    }

    /**
     * 將 JPA Entity 轉換為 Domain Profile。
     *
     * @param entity JPA Entity
     * @return 轉換後的 Domain 物件
     */
    public static Profile toDomain(ProfileEntity entity) {
        ProfileId id = new ProfileId(entity.getId());
        Email email = new Email(entity.getEmail());
        Name name = new Name(entity.getName());
        Age age = new Age(entity.getAge());
        return new Profile(id, email, name, age);
    }
}
