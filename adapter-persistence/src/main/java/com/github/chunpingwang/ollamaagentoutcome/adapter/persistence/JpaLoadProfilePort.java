package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作的 {@link LoadProfilePort}。
 */
@Component
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository profileRepository;

    /**
     * 建構子。
     *
     * @param profileRepository 個人資料 Repository
     */
    JpaLoadProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return profileRepository.findById(id.getValue())
                .map(ProfileEntity::toDomain);
    }
}
