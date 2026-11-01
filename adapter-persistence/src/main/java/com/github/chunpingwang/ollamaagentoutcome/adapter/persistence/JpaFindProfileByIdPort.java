package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 使用 JPA 實作的 FindProfileById Port。
 */
@Component
public class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final ProfileRepository profileRepository;

    JpaFindProfileByIdPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        UUID uuidId = UUID.fromString(id.value());
        return profileRepository.findById(uuidId)
                .map(ProfileEntityConverter::toDomain);
    }
}
