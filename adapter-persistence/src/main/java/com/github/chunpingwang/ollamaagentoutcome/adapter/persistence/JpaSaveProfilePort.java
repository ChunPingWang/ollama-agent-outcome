package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * 使用 JPA 實作的儲存個人資料 Port。
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void save(Profile profile) {
        try {
            var entity = toEntity(profile);
            // 先檢查是否已存在，若存在則更新欄位以維持受管實體狀態並避免 isNew=true 導致的 INSERT 衝突
            if (profileRepository.existsById(entity.getId())) {
                var existing = profileRepository.findById(entity.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found for update: " + entity.getId()));
                existing.setEmail(entity.getEmail());
                existing.setDisplayName(entity.getDisplayName());
                profileRepository.saveAndFlush(existing);
            } else {
                profileRepository.saveAndFlush(entity);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }

    private ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
            profile.id().value(),
            profile.email().address(),
            profile.displayName() != null ? profile.displayName().name() : null
        );
    }
}
