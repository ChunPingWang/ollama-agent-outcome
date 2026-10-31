package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * Port 實作類別，透過 JPA Repository 儲存 Domain Profile 並處理約束例外。
 */
@Service
class JpaSaveProfilePort implements SaveProfilePort {

    private static final Logger log = LoggerFactory.getLogger(JpaSaveProfilePort.class);

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = convertToEntity(profile);
            // 使用 saveAndFlush 確保在測試與交易內立即觸發唯一性約束
            ProfileEntity savedEntity = repository.save(entity);
            return convertToDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    private ProfileEntity convertToEntity(Profile profile) {
        String bioValue = profile.bio() != null ? profile.bio().value() : "";
        return new ProfileEntity(
                profile.id().value(),
                profile.name().value(),
                profile.email().value(),
                bioValue
        );
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
