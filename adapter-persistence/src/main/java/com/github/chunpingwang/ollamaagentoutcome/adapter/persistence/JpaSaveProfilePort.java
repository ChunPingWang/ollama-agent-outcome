package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA 實作 SaveProfilePort，負責將 Domain Profile 持久化並處理唯一性衝突。
 */
@Component
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            var entity = ProfileConverter.toEntity(profile);
            return ProfileConverter.toDomain(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists: " + profile.email().value(), e);
        }
    }
}
