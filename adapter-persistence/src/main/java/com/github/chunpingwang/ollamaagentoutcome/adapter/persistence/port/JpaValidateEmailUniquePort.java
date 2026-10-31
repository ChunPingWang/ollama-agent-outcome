package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ValidateEmailUniquePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA 實作的 Validate Email Unique Port。
 */
@Component
public class JpaValidateEmailUniquePort implements ValidateEmailUniquePort {

    private final ProfileJpaRepository repository;

    public JpaValidateEmailUniquePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isUnique(String email, UUID excludeId) {
        if (excludeId == null) {
            return !repository.existsByEmail(email);
        } else {
            return !repository.existsByEmailAndIdNot(email, excludeId);
        }
    }
}
