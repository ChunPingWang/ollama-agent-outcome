package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 使用 JPA 實作 DeleteProfilePort。
 */
class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository profileRepository;

    JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(ProfileId id) {
        profileRepository.deleteById(id.value());
    }
}
