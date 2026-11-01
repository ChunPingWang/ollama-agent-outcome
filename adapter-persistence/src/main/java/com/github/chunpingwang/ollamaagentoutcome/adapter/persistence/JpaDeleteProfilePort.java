package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

/**
 * JPA 實作的 DeleteProfile Port。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        String uuidStr = id.id().toString();
        // 先檢查是否存在，若不存在則拋出例外（由 UseCase 層處理或傳播）
        if (!repository.existsById(uuidStr)) {
             throw new jakarta.persistence.EntityNotFoundException("Profile not found with id: " + uuidStr);
        }
        repository.deleteById(uuidStr);
    }
}
