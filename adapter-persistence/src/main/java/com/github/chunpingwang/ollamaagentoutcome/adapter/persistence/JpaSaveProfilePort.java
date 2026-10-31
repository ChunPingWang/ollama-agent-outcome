package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 使用 JPA 實作的 SaveProfilePort。
 */
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;
    private final ProfileConverter converter;

    public JpaSaveProfilePort(ProfileRepository repository, ProfileConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Profile save(Profile profile) {
        // 檢查電子郵件是否重複
        // 如果是更新操作，profile.getId() 不為 null，需排除自身
        // 但由於 Domain 層生成 ID，這裡假設每次 save 都是新物件或明確的更新
        // 為了簡化，我們檢查 email 是否存在，如果存在且 ID 不同，則視為重複
        // 注意：由於 Profile 是 immutable record，每次更新都會產生新的 Profile 實例（帶有相同 ID）
        // 但 JPA 的 save 行為取決於 isNew()。
        // 這裡我們先檢查唯一性約束
        
        repository.findByEmail(profile.getEmail().value())
            .ifPresent(existingEntity -> {
                if (!existingEntity.getId().equals(profile.getId().id())) {
                    throw new DuplicateEmailException(profile.getEmail());
                }
            });

        ProfileEntity entity = converter.toEntity(profile);
        ProfileEntity savedEntity = repository.save(entity);
        return converter.toDomain(savedEntity);
    }
}
