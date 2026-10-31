package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 使用 JPA 刪除個人資料的 Port 實作。
 */
@Repository
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        if (!repository.existsById(id.value())) {
            throw new ProfileNotFoundException("Profile not found: " + id.value());
        }
        repository.deleteById(id.value());
    }
}
