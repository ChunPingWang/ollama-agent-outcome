package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA 實作的 Load Port，負責從資料庫載入資料並轉換為 Domain Entity。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;
    private final ProfileEntityConverter converter;

    public JpaLoadProfilePort(ProfileJpaRepository repository, ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> findById(ProfileId id) {
        return repository.findById(id.id().toString())
                .map(converter::toDomain);
    }
}
