package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * 使用 JPA 實作 SaveProfile Port。
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void save(Profile profile) {
        try {
            ProfileEntity entity = toEntity(profile);
            // Use saveAndFlush to ensure constraints are checked immediately within the transaction
            profileRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email address: " + profile.getEmail().getAddress());
        }
    }

    /**
     * Convert Domain Profile to JPA Entity.
     */
    private static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.getId().getValue());
        entity.setEmail(profile.getEmail().getAddress());
        entity.setDisplayName(profile.getDisplayName());
        // When converting from domain, it's considered a new transient state 
        // unless explicitly managed by the repository save logic which handles isNew flag internally via @PostLoad/Persist.
        // However, for explicit creation flow:
        entity.isNew = true; 
        
        return entity;
    }
}
