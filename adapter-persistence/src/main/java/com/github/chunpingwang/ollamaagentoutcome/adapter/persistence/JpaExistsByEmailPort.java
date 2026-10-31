package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;

/**
 * JPA 實作的 ExistsByEmail Port，負責檢查電子郵件是否已存在。
 */
@Component
class JpaExistsByEmailPort implements ExistsByEmailPort {

    private final ProfileRepository profileRepository;

    public JpaExistsByEmailPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return profileRepository.existsByEmail(email.value());
    }
}
