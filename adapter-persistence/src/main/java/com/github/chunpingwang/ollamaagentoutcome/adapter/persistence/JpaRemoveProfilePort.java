package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA implementation of RemoveProfilePort.
 */
@Service
public class JpaRemoveProfilePort implements RemoveProfilePort {

    private final ProfileRepository repository;

    JpaRemoveProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.id());
    }
}
