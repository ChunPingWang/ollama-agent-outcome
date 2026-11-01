package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * 使用 JPA 實作 DeleteProfile Port。
 */
@Repository
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository profileRepository;

    public JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(String id) {
        // We don't check existence here because the Application Service (DeleteProfileService) 
        // already verified it exists via LoadProfilePort and throws ProfileNotFoundException if not found.
        try {
            profileRepository.deleteById(id);
        } catch (DataAccessException e) {
            // If something goes wrong at DB level, let it propagate or wrap appropriately.
            throw new RuntimeException("Failed to delete profile with ID: " + id, e);
        }
    }
}
