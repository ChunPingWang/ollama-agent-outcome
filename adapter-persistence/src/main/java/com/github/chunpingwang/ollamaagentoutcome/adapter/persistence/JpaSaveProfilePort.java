package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;

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
            ProfileEntity entity = toEntity(profile);
            // useAndFlush 確保在測試交易內立即觸發唯一性約束檢查
            ProfileEntity saved = repository.saveAndFlush(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    private ProfileEntity toEntity(com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile) {
        if (profile == null) {
            return null;
        }
        return new ProfileEntity(
                profile.id(),
                profile.email().value(),
                profile.displayName() != null ? profile.displayName().value() : null
        );
    }

    private com.github.chunpingwang.ollamaagentoutcome.domain.Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        Email email = new Email(entity.getEmail());
        DisplayName displayName = entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null;
        return new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                entity.getId(),
                email,
                displayName
        );
    }
}
