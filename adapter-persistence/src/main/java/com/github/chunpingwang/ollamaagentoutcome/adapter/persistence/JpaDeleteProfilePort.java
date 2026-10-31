package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA Delete Port 實作：負責根據 ID 刪除個人資料。
 */
@Transactional
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final EntityManager entityManager;

    public JpaDeleteProfilePort(EntityManager entityManager) {
        this.entityManager = requireNonNull(entityManager);
    }

    @Override
    public void deleteById(ProfileId id) {
        ProfileJpaEntity entity = entityManager.find(ProfileJpaEntity.class, id.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
