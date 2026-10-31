package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA implementation of DeleteProfilePort.
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new ProfileNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
