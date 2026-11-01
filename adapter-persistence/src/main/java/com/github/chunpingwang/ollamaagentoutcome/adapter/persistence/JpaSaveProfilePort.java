package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作 SaveProfile Port。
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
            var entity = toEntity(profile);
            // 使用 saveAndFlush 以在測試交易內立即觸發唯一性約束檢查
            var savedEntity = repository.saveAndFlush(entity);
            return toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email: " + profile.getEmail().value());
        }
    }

    private ProfileJpaEntity toEntity(Profile profile) {
        return new ProfileJpaEntity(
                profile.getId().id().toString(),
                profile.getEmail().value(),
                profile.getDisplayName() != null ? profile.getDisplayName().value() : null
        );
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
