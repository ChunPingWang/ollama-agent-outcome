package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * Port implementation to check email existence via JPA.
 */
@Service
class JpaExistsByEmailPort implements ExistsByEmailPort {

    private final ProfileJpaRepository repository;

    public JpaExistsByEmailPort(ProfileJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
