package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA 實作的儲存個人資料埠。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;

    public JpaSaveProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = toEntity(profile);
            repository.saveAndFlush(entity);
            return fromEntity(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("unknown");
        }
    }

    private ProfileEntity toEntity(Profile profile) {
        UUID id = profile.id().value();
        String email = profile.email().address();
        String displayName = profile.displayName() != null ? profile.displayName().name() : null;
        String bio = profile.bio() != null ? profile.bio().content() : null;
        return new ProfileEntity(id, email, displayName, bio);
    }

    private Profile fromEntity(ProfileEntity entity) {
        // Domain objects are immutable records/value-objects in this design.
        // We reconstruct them from the persisted state.
        com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId id = 
                new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(entity.getId());
        
        com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email email = 
                new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email(entity.getEmail());
                
        com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName displayName = 
                entity.getDisplayName() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName(entity.getDisplayName()) : null;
                
        com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio bio = 
                entity.getBio() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio(entity.getBio()) : null;
                
        return new com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile(id, email, displayName, bio);
    }
}
