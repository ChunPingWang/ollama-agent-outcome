package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * FindProfileByIdPort 的 JPA 實作，負責從資料庫查詢並轉換為領域物件。
 */
@Repository
class JpaFindProfileByIdPort implements FindProfileByIdPort {

    private final ProfileRepository profileRepository;

    public JpaFindProfileByIdPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return profileRepository.findById(id.value())
            .map(entity -> new Profile(
                new ProfileId(entity.getId()), 
                new Name(entity.getName()), 
                new Email(entity.getEmail())
            ));
    }
}
