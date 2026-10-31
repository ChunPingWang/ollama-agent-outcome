package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 實作 SaveProfilePort，負責儲存與轉換邏輯。
 */
@Component
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;
    private final ProfileEntityConverter converter;

    public JpaSaveProfilePort(ProfileRepository repository, 
                              ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Profile save(Profile profile) {
        try {
            var entity = converter.toEntity(profile);
            // 使用 saveAndFlush 確保在單一交易內觸發唯一性約束檢查
            repository.saveAndFlush(entity);
            return converter.toDomain(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("電子郵件已存在");
        }
    }
}
