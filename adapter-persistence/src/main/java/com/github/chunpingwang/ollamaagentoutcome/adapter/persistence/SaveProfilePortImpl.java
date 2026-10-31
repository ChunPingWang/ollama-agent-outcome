package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * Implementation of SaveProfilePort using JPA Repository.
 */
@Component
public class SaveProfilePortImpl implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public SaveProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Profile profile) {
        try {
            ProfileEntity entity = new ProfileEntity(
                profile.getId().value(),
                profile.getEmail().getValue(),
                profile.getDisplayName() != null ? profile.getDisplayName().getValue() : null
            );
            // Ensure it is treated as a new record for INSERT logic if not yet persisted
            // The constructor sets isNew=true, which triggers INSERT via Persistable interface
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }
}
