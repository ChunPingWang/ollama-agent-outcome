package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 使用 JPA 實作 DeleteProfilePort，負責刪除個人資料。
 */
@Component
class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository repository;

    public JpaDeleteProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
