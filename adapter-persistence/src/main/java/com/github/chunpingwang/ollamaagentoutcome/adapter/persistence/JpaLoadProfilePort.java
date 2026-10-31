package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 使用 JPA 載入個人資料的 Port 實作。
 */
@Repository
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;
    private final ProfileEntityConverter converter;

    public JpaLoadProfilePort(ProfileJpaRepository repository, ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value())
                .map(converter::toDomain);
    }
}
