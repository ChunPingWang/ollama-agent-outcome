package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 使用 JPA 實作的載入個人資料 Port。
 */
@Repository
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository profileRepository;

    public JpaLoadProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return profileRepository.findById(id).map(this::toDomain);
    }

    private Profile toDomain(ProfileEntity entity) {
        var email = new Email(entity.getEmail());
        DisplayName displayName = null;
        if (entity.getDisplayName() != null && !entity.getDisplayName().isBlank()) {
            displayName = new DisplayName(entity.getDisplayName());
        } else {
            // Use a valid empty/null representation, though constructor handles blank -> null
             displayName = new DisplayName(null); 
        }
        return Profile.create(new ProfileId(entity.getId()), email, displayName);
    }
}
