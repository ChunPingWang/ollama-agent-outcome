package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA 實作的 LoadProfile Port，負責根據 ID 載入個人資料。
 */
@Component
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository profileRepository;

    public JpaLoadProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return profileRepository.findById(id.value())
                .map(ProfileEntityConverter::toDomain);
    }
}
