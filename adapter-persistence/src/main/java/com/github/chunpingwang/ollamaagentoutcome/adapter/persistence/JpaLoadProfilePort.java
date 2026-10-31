package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 實作 LoadProfilePort，負責載入個人資料。
 */
@Component
class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;
    private final ProfileEntityConverter converter;

    public JpaLoadProfilePort(ProfileRepository repository, 
                              ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.Optional<Profile> findById(UUID id) {
        return repository.findById(id).map(converter::toDomain);
    }
}
