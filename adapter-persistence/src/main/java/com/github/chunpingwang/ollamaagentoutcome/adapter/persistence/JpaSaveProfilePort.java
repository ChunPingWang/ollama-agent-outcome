package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * Implementation of SaveProfilePort using JPA.
 */
@Repository
public class JpaSaveProfilePort implements com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void save(Profile profile) {
        try {
            String id = profile.id().value();
            var existingEntityOpt = profileRepository.findById(id);
            
            ProfileEntity entity;
            if (existingEntityOpt.isPresent()) {
                // Update existing record by modifying fields on the managed entity
                entity = existingEntityOpt.get();
                entity.setEmail(profile.email().address());
                entity.setDisplayName(
                    profile.displayName() != null && !profile.displayName().name().isBlank() 
                        ? profile.displayName().name() 
                        : ""
                );
            } else {
                // Insert new record
                entity = ProfileConverter.toEntity(profile);
            }
            
            profileRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email address");
        }
    }
}
