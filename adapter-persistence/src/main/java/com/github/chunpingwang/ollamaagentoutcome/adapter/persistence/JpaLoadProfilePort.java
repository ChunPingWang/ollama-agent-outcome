package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 使用 JPA 實作 LoadProfile Port。
 */
@Repository
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository profileRepository;

    public JpaLoadProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return profileRepository.findById(id).map(ProfileConverter::toDomain);
    }
}
