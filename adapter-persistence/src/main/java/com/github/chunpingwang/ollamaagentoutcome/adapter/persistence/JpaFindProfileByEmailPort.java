package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 依 Email 查找個人資料的 Port 實作。
 */
class JpaFindProfileByEmailPort implements FindProfileByEmailPort {

    private final ProfileRepository profileRepository;

    JpaFindProfileByEmailPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findByEmail(Email email) {
        return profileRepository.findByEmail(email.getValue())
                .map(ProfileEntityConverter::toDomain);
    }
}
