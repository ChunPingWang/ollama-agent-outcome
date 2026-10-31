package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * 使用 JPA 實作 ExistsByEmailPort。
 */
class JpaExistsByEmailPort implements ExistsByEmailPort {

    private final ProfileRepository profileRepository;

    JpaExistsByEmailPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return profileRepository.existsByEmail(email.value());
    }
}
