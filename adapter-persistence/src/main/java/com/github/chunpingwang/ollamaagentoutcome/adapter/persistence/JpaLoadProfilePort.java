package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 載入個人資料的 Port 實作。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(String profileId) {
        return repository.findById(profileId)
                .map(ProfileConverter::toDomain);
    }
}
