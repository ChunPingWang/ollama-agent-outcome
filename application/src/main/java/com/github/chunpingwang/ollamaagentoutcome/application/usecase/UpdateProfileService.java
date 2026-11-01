package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Service implementation for updating an existing user profile.
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfileByIdPort loadProfileByIdPort;
    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfileByIdPort loadProfileByIdPort, 
                                ExistsByEmailPort existsByEmailPort, 
                                SaveProfilePort saveProfilePort) {
        this.loadProfileByIdPort = loadProfileByIdPort;
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(ProfileId id, Email email, DisplayName displayName) {
        Optional<Profile> optionalProfile = loadProfileByIdPort.findById(id.id());
        
        if (optionalProfile.isEmpty()) {
            throw new ProfileNotFoundException(id.id());
        }
        
        // Check for duplicate email excluding the current ID being updated
        if (existsByEmailPort.existsByEmail(email.address(), id.id())) {
            throw new DuplicateEmailException(email.address());
        }
        
        Profile existingProfile = optionalProfile.get();
        Profile updatedProfile = existingProfile.update(email, displayName);
        
        saveProfilePort.save(updatedProfile);
    }
}
