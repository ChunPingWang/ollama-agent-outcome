package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA 實作的 Remove Profile Port。
 */
@Component
public class JpaRemoveProfilePort implements RemoveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaRemoveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void remove(ProfileId id) {
        repository.deleteById(id.value());
    }
}
