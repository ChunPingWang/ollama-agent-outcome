package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.converter.ProfileConverter;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import java.util.Optional;

/**
 * 使用 JPA 實現 LoadProfilePort。
 */
public class JpaLoadProfilePort implements LoadProfilePort {

    private final JpaProfileRepository repository;

    public JpaLoadProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value())
                .map(ProfileConverter::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.address());
    }
}
