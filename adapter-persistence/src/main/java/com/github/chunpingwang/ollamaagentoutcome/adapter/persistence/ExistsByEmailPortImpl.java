package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * Implementation of ExistsByEmailPort using JPA Repository.
 */
@Component
public class ExistsByEmailPortImpl implements ExistsByEmailPort {

    private final ProfileJpaRepository repository;

    public ExistsByEmailPortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean exists(Email email) {
        return repository.existsByEmail(email.getValue());
    }
}
