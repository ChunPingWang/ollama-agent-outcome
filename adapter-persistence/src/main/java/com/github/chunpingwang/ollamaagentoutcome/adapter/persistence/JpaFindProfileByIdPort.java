package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA implementation of FindProfileByIdPort.
 */
@Component
public class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final ProfileJpaRepository repository;

    public JpaFindProfileByIdPort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return repository.findById(id).map(ProfileConverter::toDomain);
    }
}
