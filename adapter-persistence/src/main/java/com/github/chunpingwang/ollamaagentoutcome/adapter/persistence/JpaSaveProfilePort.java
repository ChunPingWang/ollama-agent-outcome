package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * Implementation of SaveProfilePort using JPA.
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            String id = profile.id().value();
            
            // 先查詢現有實體，若存在則更新欄位並回傳 Domain Object；否則建立新 Entity 後儲存
            return profileRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setEmail(profile.email().getAddress());
                    existingEntity.setDisplayName(profile.displayName().getName());
                    
                    // saveAndFlush 會將受管實體寫入資料庫，再轉換為領域物件回傳
                    return ProfileEntityConverter.toDomain(profileRepository.saveAndFlush(existingEntity));
                })
                .orElseGet(() -> {
                    // 新增情境：透過 Converter 建立帶有 isNew=true 的 Entity
                    ProfileEntity entity = ProfileEntityConverter.toEntity(profile);
                    return ProfileEntityConverter.toDomain(profileRepository.saveAndFlush(entity));
                });
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email address");
        }
    }
}
