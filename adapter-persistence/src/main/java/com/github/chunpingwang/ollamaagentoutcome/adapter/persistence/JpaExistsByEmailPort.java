package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * ExistsByEmailPort 的 JPA 實作。
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
