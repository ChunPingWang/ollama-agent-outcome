package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA Load Profile Port 實作，負責從資料庫載入資料並轉換為 Domain Object。
 */
@Repository
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;

    JpaLoadProfilePort(ProfileRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return repository.findById(id).map(ProfileEntityConverter::toDomain);
    }
}
