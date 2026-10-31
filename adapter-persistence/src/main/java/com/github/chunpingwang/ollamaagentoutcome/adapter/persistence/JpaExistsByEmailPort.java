package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * 使用 JPA 實作 ExistsByEmailPort，檢查電子郵件是否已存在。
 */
@Component
class JpaExistsByEmailPort implements ExistsByEmailPort {

    private final ProfileRepository repository;

    public JpaExistsByEmailPort(ProfileRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
