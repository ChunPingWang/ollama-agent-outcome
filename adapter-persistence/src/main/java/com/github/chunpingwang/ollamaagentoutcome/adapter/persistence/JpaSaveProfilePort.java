package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作 SaveProfile Port。
 */
@Component("jpaSaveProfilePort")
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            String idStr = profile.id().id().toString();
            
            // 先檢查是否已存在，若存在則更新；不存在則建立
            java.util.Optional<ProfileJpaEntity> existingOpt = repository.findById(idStr);
            
            if (existingOpt.isPresent()) {
                // 更新現有實體：取得受管實體並修改欄位
                ProfileJpaEntity entity = existingOpt.get();
                entity.setEmail(profile.getEmail().value());
                entity.setDisplayName(profile.getDisplayName().isPresent() ? profile.getDisplayName().value() : null);
                
                // saveAndFlush 確保在測試交易內觸發唯一性約束
                repository.saveAndFlush(entity);
            } else {
                // 建立新實體
                ProfileJpaEntity newEntity = new ProfileJpaEntity(
                    idStr, 
                    profile.getEmail().value(), 
                    profile.getDisplayName().isPresent() ? profile.getDisplayName().value() : null
                );
                repository.saveAndFlush(newEntity);
            }
            
            return profile;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }
}
