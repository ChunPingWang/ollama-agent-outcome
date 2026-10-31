package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 儲存個人資料的 Port 實作。
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
            ProfileEntity entity = ProfileConverter.toEntity(profile);
            ProfileEntity savedEntity = repository.save(entity);
            // 確保寫入資料庫以觸發唯一約束檢查
            repository.flush();
            return ProfileConverter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            // 當 email 重複時，JPA 會拋出 DataIntegrityViolationException
            // 這裡我們將其轉換為 Domain Exception
            throw new DuplicateEmailException(profile.email().getValue());
        }
    }
}
