package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;

/**
 * 使用 JPA 實作的 DeleteProfileById Port。
 */
@Component
public class JpaDeleteProfileByIdPort implements DeleteProfileByIdPort {

    private final ProfileRepository profileRepository;

    JpaDeleteProfileByIdPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(ProfileId id) {
        UUID uuidId = UUID.fromString(id.value());
        if (profileRepository.existsById(uuidId)) {
            profileRepository.deleteById(uuidId);
        }
    }
}
