package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * JPA 實作的 Save Port，負責將 Domain Entity 轉換為 JPA Entity 並儲存。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;
    private final ProfileEntityConverter converter;

    public JpaSaveProfilePort(ProfileJpaRepository repository, ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            // 使用 saveAndFlush 以確保在測試交易內立即觸發唯一性約束檢查
            var entity = converter.toEntity(profile);
            repository.saveAndFlush(entity);
            return converter.toDomain(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(profile.getEmailValue());
        }
    }
}
