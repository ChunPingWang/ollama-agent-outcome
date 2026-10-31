package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 儲存個人資料的 Port 實作。
 */
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile save(Profile profile) {
        ProfileEntity entity = ProfileEntityConverter.toEntity(profile);
        ProfileEntity savedEntity = profileRepository.save(entity);
        return ProfileEntityConverter.toDomain(savedEntity);
    }
}
