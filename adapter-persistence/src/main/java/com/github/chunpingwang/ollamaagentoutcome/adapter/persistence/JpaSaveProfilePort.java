package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作 SaveProfile Port。支援建立與更新操作。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        String idStr = profile.getId().id().toString();
        
        // 檢查是否已存在，以決定執行 INSERT (Create) 或 UPDATE (Update)
        boolean exists = repository.existsById(idStr);
        
        try {
            if (exists) {
                // Update: 載入現有實體並更新欄位，確保 isNew=false 以觸發 UPDATE SQL
                ProfileJpaEntity existingEntity = repository.findById(idStr).orElseThrow(() -> 
                    new RuntimeException("Should exist but not found for update")
                );
                
                existingEntity.setEmail(profile.getEmail().value());
                existingEntity.setDisplayName(
                    profile.getDisplayName() != null ? profile.getDisplayName().value() : null
                );
                
                // 明確呼叫 saveAndFlush 強制寫入資料庫，解決整合測試中讀取到舊資料的問題
                return toDomain(repository.saveAndFlush(existingEntity));
            } else {
                // Create: 建立新實體，isNew=true
                var entity = toEntity(profile);
                var savedEntity = repository.saveAndFlush(entity);
                return toDomain(savedEntity);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email: " + profile.getEmail().value());
        }
    }

    private ProfileJpaEntity toEntity(Profile profile) {
        return new ProfileJpaEntity(
                profile.getId().id().toString(),
                profile.getEmail().value(),
                profile.getDisplayName() != null ? profile.getDisplayName().value() : null
        );
    }

    private Profile toDomain(ProfileJpaEntity entity) {
        return new Profile(
                new ProfileId(java.util.UUID.fromString(entity.getId())),
                new Email(entity.getEmail()),
                entity.getDisplayName() != null && !entity.getDisplayName().isBlank() 
                        ? new DisplayName(entity.getDisplayName()) 
                        : new DisplayName((String) null)
        );
    }
}
