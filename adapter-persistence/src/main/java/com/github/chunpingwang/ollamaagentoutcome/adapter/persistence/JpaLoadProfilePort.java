package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Port 實作類別，透過 JPA Repository 載入 Domain Profile。
 */
@Service
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value())
                .map(this::convertToDomain);
    }

    private Profile convertToDomain(ProfileEntity entity) {
        String bioValue = "".equals(entity.getBio()) ? null : entity.getBio();
        return new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(entity.getId()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Name(entity.getName()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email(entity.getEmail()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Bio(bioValue)
        );
    }
}
