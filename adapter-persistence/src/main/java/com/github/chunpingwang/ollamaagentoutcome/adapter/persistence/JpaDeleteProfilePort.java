package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作的 DeleteProfilePort。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository profileRepository;

    public JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(ProfileId id) {
        profileRepository.deleteById(id.id());
    }
}
