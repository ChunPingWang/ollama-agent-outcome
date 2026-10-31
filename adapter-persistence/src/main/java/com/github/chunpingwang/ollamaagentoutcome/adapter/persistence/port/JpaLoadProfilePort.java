package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile.JpaProfileConverter;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;

/**
 * JPA implementation of the LoadProfilePort.
 */
public class JpaLoadProfilePort implements LoadProfilePort {

    private final JpaProfileRepository repository;

    public JpaLoadProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile> findById(ProfileId id) {
        return repository.findById(id.value().toString())
                .map(JpaProfileConverter::toDomain);
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(JpaProfileConverter::toDomain);
    }
}
