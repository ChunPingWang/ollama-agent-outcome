package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA 實作 LoadProfilePort，負責從資料庫加載個人資料。
 */
@Component
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public java.util.Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.id())
                .map(ProfileConverter::toDomain);
    }
}
