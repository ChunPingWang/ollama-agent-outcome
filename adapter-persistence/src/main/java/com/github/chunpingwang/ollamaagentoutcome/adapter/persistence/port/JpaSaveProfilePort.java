package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * 使用 JPA 實現 SaveProfilePort，負責將 Domain Entity 持久化至資料庫。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = toEntity(profile);
            // 必須使用 saveAndFlush 以在測試交易內觸發唯一性約束檢查
            repository.saveAndFlush(entity);
            return fromEntity(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("unknown");
        }
    }

    private ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().value());
        entity.setEmail(profile.email().address());
        entity.setDisplayName(profile.displayName().name());
        entity.setBio(profile.bio() != null ? profile.bio().content() : null);
        return entity;
    }

    private Profile fromEntity(ProfileEntity entity) {
        return new Profile(
                com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId.of(entity.getId()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email(entity.getEmail()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName(entity.getDisplayName()),
                entity.getBio() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio(entity.getBio()) : null
        );
    }
}
