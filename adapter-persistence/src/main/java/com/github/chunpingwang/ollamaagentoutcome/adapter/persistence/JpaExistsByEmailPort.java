package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * 使用 JPA 檢查電子郵件是否存在的 Port 實作。
 */
@Component
public class JpaExistsByEmailPort implements ExistsByEmailPort {

    private final ProfileJpaRepository repository;

    public JpaExistsByEmailPort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
