package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * JPA 實作的 Find Profile By ID Port。
 */
@Component
public class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final ProfileJpaRepository repository;

    public JpaFindProfileByIdPort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value()).map(this::toDomain);
    }

    private Profile toDomain(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity entity) {
        var profileId = new ProfileId(entity.getId());
        var email = new Email(entity.getEmail());
        DisplayName displayName = null;
        if (entity.getDisplayName() != null && !entity.getDisplayName().isBlank()) {
            displayName = new DisplayName(entity.getDisplayName());
        }
        Bio bio = null;
        if (entity.getBio() != null && !entity.getBio().isEmpty()) {
            bio = new Bio(entity.getBio());
        } else {
             // Handle empty/null bio gracefully, though domain might prefer non-null
             // Based on existing Domain logic, Bio record allows content check. 
             // Let's stick to creating a Bio object even if empty string for consistency with constructor expectations in other parts if needed, 
             // but here we just pass what we have or default. The Profile constructor handles nulls by converting to defaults usually?
             // Looking at Profile.java: `this.bio = bio != null ? bio : new Bio("");`
             // So passing an empty Bio is safe.
             bio = new Bio("");
        }
        
        return new Profile(profileId, email, displayName, bio);
    }
}
