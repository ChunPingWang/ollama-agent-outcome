package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * JPA 實作的載入個人資料 Port。
 */
@Component
public class JpaLoadProfilePortImpl implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    JpaLoadProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.toString()).map(this::toDomain);
    }

    private Profile toDomain(ProfileEntity entity) {
        Email email = new Email(entity.getEmail());
        DisplayName displayName = (entity.getDisplayName() != null && !entity.getDisplayName().isBlank()) 
                ? new DisplayName(entity.getDisplayName()) : null;
        return new Profile(java.util.UUID.fromString(entity.getId()), email, displayName);
    }
}
