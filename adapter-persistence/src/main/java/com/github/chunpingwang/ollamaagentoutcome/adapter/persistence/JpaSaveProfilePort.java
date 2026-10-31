package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 使用 JPA 實作的 SaveProfilePort。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile save(Profile profile) {
        // 檢查電子郵件是否重複
        Optional<ProfileEntity> existingEntity = profileRepository.findByEmail(profile.email().value());
        if (existingEntity.isPresent()) {
            // 如果已存在且 ID 不同，則為重複
            if (!existingEntity.get().getId().equals(profile.id().id())) {
                throw new DuplicateEmailException(profile.email());
            }
        }

        ProfileEntity entity = ProfileEntity.fromDomain(profile);
        ProfileEntity savedEntity = profileRepository.save(entity);
        return savedEntity.toDomain();
    }
}
