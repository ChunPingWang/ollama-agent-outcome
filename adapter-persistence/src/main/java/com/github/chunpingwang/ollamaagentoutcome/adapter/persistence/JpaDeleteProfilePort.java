package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 使用 JPA 實作的 DeleteProfilePort。
 */
class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository repository;

    public JpaDeleteProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.id());
    }
}
