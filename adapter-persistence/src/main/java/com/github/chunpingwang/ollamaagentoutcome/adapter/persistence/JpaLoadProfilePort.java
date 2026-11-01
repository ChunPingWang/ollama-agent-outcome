package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA 實作的 LoadProfile Port。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        String uuidStr = id.id().toString();
        return repository.findById(uuidStr).map(this::toDomain);
    }

    /**
     * 將 JPA Entity 轉換為 Domain Model。
     */
    private Profile toDomain(ProfileJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        ProfileId profileId = new ProfileId(java.util.UUID.fromString(entity.getId()));
        Email email = new Email(entity.getEmail());
        DisplayName displayName = (entity.getDisplayName() != null && !entity.getDisplayName().isBlank()) 
            ? new DisplayName(entity.getDisplayName()) 
            : new DisplayName("");
        
        return new Profile(profileId, email, displayName);
    }
}
