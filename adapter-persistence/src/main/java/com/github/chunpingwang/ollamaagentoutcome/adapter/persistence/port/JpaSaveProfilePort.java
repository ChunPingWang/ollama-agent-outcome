package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA implementation of SaveProfilePort.
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Profile profile) {
        try {
            ProfileEntity entity = toEntity(profile);
            // Use saveAndFlush to trigger constraints immediately in tests/transactions
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists: " + profile.email().address());
        }
    }

    private ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
                profile.id().value(),
                profile.email().address(),
                profile.displayName().name(),
                profile.bio().content()
        );
    }
}
