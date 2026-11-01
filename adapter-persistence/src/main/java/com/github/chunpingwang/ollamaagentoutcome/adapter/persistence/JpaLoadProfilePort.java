package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA 實作的 LoadProfile Port。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;

    public JpaLoadProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId id) {
        return repository.findById(id.id()).map(ProfileEntityConverter::toDomain);
    }
}
