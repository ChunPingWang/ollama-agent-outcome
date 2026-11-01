package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA Save Profile Port 實作，處理新增與更新邏輯及唯一性檢查。
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;

    JpaSaveProfilePort(ProfileRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void save(Profile profile) {
        UUID id = profile.id().id();
        
        // 先檢查資料庫中是否已存在該 ID
        if (repository.existsById(id)) {
            // 若存在：載入受管 Entity 並更新欄位（避免 isNew=true 導致 INSERT 衝突）
            ProfileEntity existingEntity = repository.findById(id).orElseThrow(() -> 
                new IllegalStateException("Entity should exist but was not found for update: " + id));
            
            existingEntity.setEmail(profile.email().value());
            existingEntity.setDisplayName(profile.displayName() != null ? profile.displayName().value() : null);
            
            try {
                repository.saveAndFlush(existingEntity);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEmailException("email already exists");
            }
        } else {
            // 若不存在：建立新 Entity 並儲存
            ProfileEntity entity = ProfileEntityConverter.toEntity(profile);
            try {
                repository.saveAndFlush(entity);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEmailException("email already exists");
            }
        }
    }
}
