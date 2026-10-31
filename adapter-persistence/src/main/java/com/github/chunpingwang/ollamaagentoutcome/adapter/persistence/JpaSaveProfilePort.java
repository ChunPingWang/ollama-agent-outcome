package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA 實作的保存個人資料 Port。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final JpaProfileRepository repository;

    public JpaSaveProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Profile profile) {
        try {
            // 使用 saveAndFlush 確保在測試交易內立即寫入並觸發唯一性約束檢查
            ProfileEntity entity = toEntity(profile);
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email detected");
        }
    }

    /**
     * 將 Domain Profile 轉換為 JPA Entity。
     * 根據 ID 是否存在於資料庫中，決定是更新現有記錄還是插入新記錄，以配合 Persistable<UUID> 的行為。
     */
    private ProfileEntity toEntity(Profile profile) {
        var existingOpt = repository.findById(profile.getId());
        if (existingOpt.isPresent()) {
            // Update scenario: load managed entity and update fields
            ProfileEntity entity = existingOpt.get();
            entity.setName(profile.getName());
            entity.setEmail(profile.getEmail());
            return entity;
        } else {
            // Create scenario: instantiate new entity with isNew=true (default in constructor/field)
            ProfileEntity entity = new ProfileEntity();
            entity.setId(profile.getId());
            entity.setName(profile.getName());
            entity.setEmail(profile.getEmail());
            return entity;
        }
    }
}
