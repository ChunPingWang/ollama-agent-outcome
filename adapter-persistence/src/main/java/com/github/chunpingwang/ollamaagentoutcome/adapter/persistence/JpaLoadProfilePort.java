package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA 實作的載入個人資料 Port。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    /**
     * 建構子注入 Repository。
     *
     * @param repository Spring Data Repository
     */
    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        UUID uuid = id.id();
        return repository.findById(uuid.toString()).map(this::toDomain);
    }

    private Profile toDomain(ProfileJpaEntity entity) {
        return new Profile(
            new ProfileId(UUID.fromString(entity.getId())),
            new Email(entity.getEmail()),
            new DisplayName(entity.getDisplayName())
        );
    }
}
