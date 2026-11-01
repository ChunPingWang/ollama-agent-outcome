package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * JPA 實作的 SaveProfile Port。
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
            // 轉換為 Entity
            String id = profile.getId().id().toString();
            Email emailVal = profile.getEmail();
            DisplayName displayNameVal = profile.getDisplayName();
            
            ProfileJpaEntity entity = new ProfileJpaEntity(
                id, 
                emailVal.value(), 
                (displayNameVal != null && !displayNameVal.value().isBlank()) ? displayNameVal.value() : null
            );
            
            // 使用 saveAndFlush 以在測試交易中立即觸發唯一性約束檢查
            ProfileJpaEntity savedEntity = repository.saveAndFlush(entity);
            
            // 回傳新的領域模型（從持久化後的實體重建，確保狀態一致）
            return toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }

    /**
     * 將 JPA Entity 轉換為 Domain Model。
     */
    private Profile toDomain(ProfileJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        ProfileId id = new ProfileId(java.util.UUID.fromString(entity.getId()));
        Email email = new Email(entity.getEmail());
        DisplayName displayName = (entity.getDisplayName() != null && !entity.getDisplayName().isBlank()) 
            ? new DisplayName(entity.getDisplayName()) 
            : new DisplayName("");
        
        return new Profile(id, email, displayName);
    }
}
