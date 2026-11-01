package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * FindProfileByIdPort 的 JPA 實作。
 */
@Component
public class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final ProfileRepository repository;
    private final ProfileEntityConverter converter;

    public JpaFindProfileByIdPort(ProfileRepository repository, ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> findById(ProfileId id) {
        return repository.findById(id.value())
                .map(converter::toDomain);
    }
}
