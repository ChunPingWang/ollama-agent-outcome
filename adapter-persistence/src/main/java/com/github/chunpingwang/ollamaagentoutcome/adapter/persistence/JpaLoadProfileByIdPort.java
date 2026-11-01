package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfileByIdPort;

/**
 * JPA implementation of LoadProfileByIdPort.
 */
@Repository
public class JpaLoadProfileByIdPort implements LoadProfileByIdPort {

    private final ProfileRepository profileRepository;

    public JpaLoadProfileByIdPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return profileRepository.findById(id).map(ProfileEntityConverter::toDomain);
    }
}
