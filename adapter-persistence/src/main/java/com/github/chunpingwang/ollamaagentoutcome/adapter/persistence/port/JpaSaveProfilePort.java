package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.converter.ProfileConverter;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * 使用 JPA 實現 SaveProfilePort，負責持久化與異常轉換。
 */
public class JpaSaveProfilePort implements SaveProfilePort {

    private final JpaProfileRepository repository;

    public JpaSaveProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = ProfileConverter.toEntity(profile);
            // 使用 saveAndFlush 確保在測試事務中立即觸發約束檢查
            ProfileEntity saved = repository.saveAndFlush(entity);
            return ProfileConverter.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.value());
    }
}
