package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 使用 JPA 實作 SaveProfilePort。
 */
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;
    private final ProfileConverter profileConverter;

    JpaSaveProfilePort(ProfileRepository profileRepository, ProfileConverter profileConverter) {
        this.profileRepository = profileRepository;
        this.profileConverter = profileConverter;
    }

    @Override
    public Profile save(Profile profile) {
        ProfileEntity entity = profileConverter.toEntity(profile);
        ProfileEntity savedEntity = profileRepository.save(entity);
        return profileConverter.toDomain(savedEntity);
    }
}
