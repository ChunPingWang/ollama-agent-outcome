package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * Implementation of DeleteProfilePort using JPA Repository.
 */
@Component
public class DeleteProfilePortImpl implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public DeleteProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.value());
    }
}
