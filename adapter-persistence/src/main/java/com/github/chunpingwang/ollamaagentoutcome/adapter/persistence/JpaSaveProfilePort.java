package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA Save Port 實作：處理個人資料的建立與更新。
 */
@Transactional
public class JpaSaveProfilePort implements SaveProfilePort {

    private final EntityManager entityManager;
    private final ProfileEntityConverter converter = new ProfileEntityConverter();

    public JpaSaveProfilePort(EntityManager entityManager) {
        this.entityManager = requireNonNull(entityManager);
    }

    @Override
    public Profile save(Profile profile) {
        try {
            // 使用 saveAndFlush 確保在測試交易內立即觸發唯一性約束檢查
            ProfileJpaEntity entity = converter.toEntity(profile);
            ProfileJpaEntity savedEntity = entityManager.merge(entity);
            entityManager.flush(); 
            return converter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email: " + profile.getEmail().getValue(), e);
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            // PostgreSQL 在某些情況下會直接拋出 Hibernate 的 ConstraintViolationException，需一併攔截轉為 Domain Exception
            throw new DuplicateEmailException("Duplicate email: " + profile.getEmail().getValue(), e);
        }
    }
}
