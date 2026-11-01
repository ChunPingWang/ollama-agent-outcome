package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 載入個人資料 Port 的實作類別。
 */
@Repository
public class LoadProfilePortImpl implements LoadProfilePort {

    private final ProfileConverter converter = new ProfileConverter();
    private final ProfileJpaRepository repository;

    public LoadProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return repository.findById(id).map(converter::toDomain);
    }
}
