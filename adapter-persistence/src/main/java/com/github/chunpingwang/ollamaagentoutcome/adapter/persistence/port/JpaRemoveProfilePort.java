package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import org.springframework.dao.EmptyResultDataAccessException;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;

/**
 * JPA implementation of the RemoveProfilePort.
 */
public class JpaRemoveProfilePort implements RemoveProfilePort {

    private final JpaProfileRepository repository;

    public JpaRemoveProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        try {
            repository.deleteById(id.value().toString());
        } catch (EmptyResultDataAccessException e) {
            // Let it propagate or wrap if needed by application logic
            throw new RuntimeException("Failed to delete profile: " + id.value(), e);
        }
    }
}
