package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * 使用 JPA 實現 LoadProfilePort，負責從資料庫載入個人資料。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value())
                .map(this::fromEntity);
    }

    private Profile fromEntity(ProfileEntity entity) {
        return new Profile(
                ProfileId.of(entity.getId()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email(entity.getEmail()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName(entity.getDisplayName()),
                entity.getBio() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio(entity.getBio()) : null
        );
    }
}
