package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 使用 JPA 依 ID 查找個人資料的 Port 實作。
 */
class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final ProfileRepository profileRepository;

    JpaFindProfileByIdPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return profileRepository.findById(id.getValue())
                .map(ProfileEntityConverter::toDomain);
    }
}
