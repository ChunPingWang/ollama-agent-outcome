package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import org.springframework.stereotype.Repository;

/**
 * 檢查電子郵件是否存在 Port 的實作類別。
 */
@Repository
public class ExistsByEmailPortImpl implements ExistsByEmailPort {

    private final ProfileJpaRepository repository;

    public ExistsByEmailPortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
