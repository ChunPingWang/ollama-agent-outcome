package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 實作的 SaveProfilePort。
 */
@Component
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = ProfileConverter.toEntity(profile);
            ProfileEntity savedEntity = profileRepository.saveAndFlush(entity);
            return ProfileConverter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email already exists", e);
        }
    }
}
