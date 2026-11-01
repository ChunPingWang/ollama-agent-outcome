package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * JPA 實作的儲存個人資料 Port。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    /**
     * 建構子注入 Repository。
     *
     * @param repository Spring Data Repository
     */
    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileJpaEntity entity = toEntity(profile);
            // 使用 saveAndFlush 以在測試交易內觸發唯一性約束
            ProfileJpaEntity saved = repository.saveAndFlush(entity);
            return toDomain(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new DuplicateEmailException("duplicate email");
        }
    }

    private ProfileJpaEntity toEntity(Profile profile) {
        UUID uuid = profile.getId().id();
        String idStr = uuid.toString();
        
        // 檢查是否已存在於資料庫中（用於更新場景）
        var existingOpt = repository.findById(idStr);
        if (existingOpt.isPresent()) {
            ProfileJpaEntity existing = existingOpt.get();
            existing.setEmail(profile.getEmail().getValue());
            existing.setDisplayName(profile.getDisplayName().value());
            return existing;
        } else {
            // 新資料，建立新的 Entity 實例並標記為 isNew
            return new ProfileJpaEntity(
                idStr, 
                profile.getEmail().getValue(), 
                profile.getDisplayName().value()
            );
        }
    }

    private Profile toDomain(ProfileJpaEntity entity) {
        return new Profile(
            new ProfileId(UUID.fromString(entity.getId())),
            new Email(entity.getEmail()),
            new DisplayName(entity.getDisplayName())
        );
    }
}
