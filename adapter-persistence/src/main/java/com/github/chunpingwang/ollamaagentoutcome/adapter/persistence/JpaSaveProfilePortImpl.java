package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * JPA 實作的儲存個人資料 Port。
 */
@Component
public class JpaSaveProfilePortImpl implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    JpaSaveProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(UUID id, Email email, DisplayName displayName) {
        String idStr = id.toString();
        
        // 先檢查是否已存在該 ID，以決定是更新還是新增
        var existingOpt = repository.findById(idStr);
        
        if (existingOpt.isPresent()) {
            // 更新模式：使用受管實體進行更新
            ProfileEntity entity = existingOpt.get();
            entity.setEmail(email.value());
            entity.setDisplayName(displayName != null ? displayName.value() : null);
            try {
                repository.saveAndFlush(entity);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEmailException("電子郵件已存在: " + email.value());
            }
        } else {
            // 新增模式：建立新實體並保存
            ProfileEntity newEntity = new ProfileEntity(idStr, email.value(), 
                displayName != null ? displayName.value() : null);
            try {
                repository.saveAndFlush(newEntity);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEmailException("電子郵件已存在: " + email.value());
            }
        }
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}
