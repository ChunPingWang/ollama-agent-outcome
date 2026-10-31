package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA implementation of LoadProfilePort.
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    private Profile toDomain(ProfileEntity entity) {
        return new Profile(
                new ProfileId(entity.getId()),
                new Email(entity.getEmail()),
                new DisplayName(entity.getDisplayName()),
                new Bio(entity.getBio())
        );
    }
}
