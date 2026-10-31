package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 使用 JPA 實作的 LoadProfilePort。
 */
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;
    private final ProfileConverter converter;

    public JpaLoadProfilePort(ProfileRepository repository, ProfileConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.id())
                .map(converter::toDomain);
    }
}
