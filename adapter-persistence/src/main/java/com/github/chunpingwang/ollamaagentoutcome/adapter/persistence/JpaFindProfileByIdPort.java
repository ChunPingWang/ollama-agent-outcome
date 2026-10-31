package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA 實作的依 ID 查詢個人資料 Port。
 */
@Component
public class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final JpaProfileRepository repository;

    public JpaFindProfileByIdPort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return repository.findById(id).map(this::fromEntity);
    }

    private Profile fromEntity(ProfileEntity entity) {
        return new Profile(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }
}
