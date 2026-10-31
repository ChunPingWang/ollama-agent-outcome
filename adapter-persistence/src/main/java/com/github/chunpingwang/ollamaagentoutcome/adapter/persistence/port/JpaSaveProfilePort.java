package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile.JpaProfileConverter;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;

/**
 * JPA implementation of the SaveProfilePort.
 */
public class JpaSaveProfilePort implements SaveProfilePort {

    private final JpaProfileRepository repository;

    public JpaSaveProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = JpaProfileConverter.toEntity(profile);
            // Use saveAndFlush to trigger unique constraint violations immediately in tests/transactions
            ProfileEntity savedEntity = repository.saveAndFlush(entity);
            return JpaProfileConverter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email detected during save", e);
        }
    }
}
