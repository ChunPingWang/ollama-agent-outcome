package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA Load Port 實作：負責根據 ID 查詢個人資料。
 */
@Transactional(readOnly = true)
public class JpaLoadProfilePort implements LoadProfilePort {

    private final EntityManager entityManager;
    private final ProfileEntityConverter converter = new ProfileEntityConverter();

    public JpaLoadProfilePort(EntityManager entityManager) {
        this.entityManager = requireNonNull(entityManager);
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        ProfileJpaEntity entity = entityManager.find(ProfileJpaEntity.class, id.getId());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(entity));
    }
}
