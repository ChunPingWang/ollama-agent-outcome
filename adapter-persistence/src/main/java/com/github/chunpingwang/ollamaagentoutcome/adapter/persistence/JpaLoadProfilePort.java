package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import java.util.Optional;

/**
 * 使用 JPA 實作 LoadProfilePort。
 */
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository profileRepository;
    private final ProfileConverter profileConverter;

    JpaLoadProfilePort(ProfileRepository profileRepository, ProfileConverter profileConverter) {
        this.profileRepository = profileRepository;
        this.profileConverter = profileConverter;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return profileRepository.findById(id.value())
                .map(profileConverter::toDomain);
    }
}
