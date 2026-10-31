package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA 實作的載入個人資料埠。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileJpaRepository repository;

    public JpaLoadProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value()).map(this::fromEntity);
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.value());
    }

    private Profile fromEntity(ProfileEntity entity) {
        ProfileId profileId = new ProfileId(entity.getId());
        Email email = new Email(entity.getEmail());
        DisplayName displayName = entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null;
        Bio bio = entity.getBio() != null ? new Bio(entity.getBio()) : null;
        
        return new Profile(profileId, email, displayName, bio);
    }
}
