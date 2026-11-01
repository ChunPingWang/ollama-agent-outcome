package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 使用 JPA 實作 LoadProfile Port。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.id().toString()).map(this::toDomain);
    }

    private Profile toDomain(ProfileJpaEntity entity) {
        return new Profile(
                new ProfileId(java.util.UUID.fromString(entity.getId())),
                new Email(entity.getEmail()),
                entity.getDisplayName() != null && !entity.getDisplayName().isBlank() 
                        ? new DisplayName(entity.getDisplayName()) 
                        : new DisplayName((String) null)
        );
    }
}
