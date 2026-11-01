package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.stereotype.Repository;

/**
 * Implementation of LoadProfilePort using JPA.
 */
@Repository
public class JpaLoadProfilePort implements com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort {

    private final ProfileRepository profileRepository;

    public JpaLoadProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public java.util.Optional<Profile> findById(String id) {
        return profileRepository.findById(id).map(ProfileConverter::toDomain);
    }
}
