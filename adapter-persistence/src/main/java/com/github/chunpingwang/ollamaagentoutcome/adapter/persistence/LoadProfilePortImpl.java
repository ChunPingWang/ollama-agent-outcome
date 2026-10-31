package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * Implementation of LoadProfilePort using JPA Repository.
 */
@Component
class LoadProfilePortImpl implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public LoadProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value())
                .map(this::convertToDomain);
    }

    private Profile convertToDomain(ProfileEntity entity) {
        Email email = new Email(entity.getEmail());
        DisplayName displayName = (entity.getDisplayName() != null && !entity.getDisplayName().isBlank()) 
                ? new DisplayName(entity.getDisplayName()) 
                : new DisplayName(null); // Normalize blank/null to empty string inside VO
        
        return new Profile(
            new ProfileId(entity.getId()),
            email,
            displayName
        );
    }
}
