package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 使用 JPA 實作的 SaveProfile Port。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void save(Profile profile) {
        try {
            UUID id = profile.getIdAsUUID();
            Optional<ProfileEntity> existingOpt = profileRepository.findById(id);

            if (existingOpt.isPresent()) {
                // 更新模式：取得受管實體並更新欄位
                ProfileEntity entity = existingOpt.get();
                entity.setEmail(profile.email().address());
                entity.setDisplayName(profile.displayName() != null ? profile.displayName().name() : null);
                profileRepository.saveAndFlush(entity);
            } else {
                // 新增模式：轉換並儲存
                ProfileEntity entity = new ProfileEntity(id);
                entity.setEmail(profile.email().address());
                entity.setDisplayName(profile.displayName() != null ? profile.displayName().name() : null);
                profileRepository.saveAndFlush(entity);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email address", e);
        }
    }
}
