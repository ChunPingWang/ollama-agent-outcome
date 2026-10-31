package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * JPA 實作 DeleteProfilePort，負責刪除個人資料並驗證存在性。
 */
@Component
class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        if (repository.findById(id.id()).isEmpty()) {
            throw new ProfileNotFoundException("profile not found: " + id.id());
        }
        repository.deleteById(id.id());
    }
}
