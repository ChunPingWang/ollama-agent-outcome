package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA implementation of SaveProfilePort.
 * Handles persistence and converts database constraint violations into domain exceptions.
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = ProfileConverter.toEntity(profile);
            // Use saveAndFlush to ensure constraints are checked immediately within transaction
            ProfileEntity savedEntity = repository.saveAndFlush(entity);
            return ProfileConverter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }
}
