package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import org.springframework.stereotype.Component;

/**
 * JPA 實作的 ExistsByEmail Port，檢查電子郵件是否存在。
 */
@Component
public class JpaExistsByEmailPort implements ExistsByEmailPort {

    private final ProfileRepository repository;

    public JpaExistsByEmailPort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}
