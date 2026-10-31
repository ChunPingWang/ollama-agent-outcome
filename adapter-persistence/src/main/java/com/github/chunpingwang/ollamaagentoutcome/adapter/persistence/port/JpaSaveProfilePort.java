package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA 實作的 Save Profile Port。
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
            var entity = toEntity(profile);
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email: " + profile.getEmail().address());
        }
    }

    private ProfileEntity toEntity(Profile profile) {
        UUID id = profile.getId().value();
        String email = profile.getEmail().address();
        String displayName = profile.getDisplayName() != null ? profile.getDisplayName().name() : null;
        String bioContent = profile.getBio() != null ? profile.getBio().content() : "";
        
        return new ProfileEntity(id, email, displayName, bioContent);
    }
}
