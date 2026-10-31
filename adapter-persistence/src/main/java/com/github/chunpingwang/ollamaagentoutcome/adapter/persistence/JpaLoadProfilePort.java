package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 使用 JPA 載入個人資料的 Port 實作。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;

    public JpaLoadProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return repository.findById(id).map(e -> ProfileEntityConverter.toDomain(Optional.of(e)).orElse(null));
    }
}
