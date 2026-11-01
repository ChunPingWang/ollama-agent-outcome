package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * JPA 實作的 SaveProfile Port，負責持久化與轉換。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;

    public JpaSaveProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile) {
        try {
            ProfileEntity entity = new ProfileEntity(
                    profile.id().value(),
                    profile.email().value(),
                    profile.displayName() != null ? profile.displayName().value() : null
            );
            // saveAndFlush 確保在交易內立即觸發唯一性約束檢查
            ProfileEntity saved = repository.saveAndFlush(entity);
            return convertToDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    static Profile convertToDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        com.github.chunpingwang.ollamaagentoutcome.domain.Email email = 
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email(entity.getEmail());
        com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName displayName = 
                entity.getDisplayName() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName(entity.getDisplayName()) : null;
        
        return new Profile(entity.getId(), email, displayName);
    }
}
