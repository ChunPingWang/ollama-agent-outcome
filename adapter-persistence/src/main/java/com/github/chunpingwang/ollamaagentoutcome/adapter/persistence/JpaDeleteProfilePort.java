package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 使用 JPA 實作 DeleteProfile Port。
 */
@Repository
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository profileRepository;

    public JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(String id) {
        profileRepository.deleteById(id);
    }
}
