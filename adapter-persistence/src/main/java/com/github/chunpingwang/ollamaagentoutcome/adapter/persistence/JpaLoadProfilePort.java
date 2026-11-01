package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA 實作：載入個人資料 Port。根據 ID 查詢並轉換為 Domain Entity。
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
        var displayNameStr = entity.getDisplayName();
        var displayName = (displayNameStr != null && !displayNameStr.isBlank()) 
            ? new DisplayName(displayNameStr) 
            : new DisplayName("");
        
        return Profile.create(new ProfileId(entity.getId()), email, displayName);
    }
}
