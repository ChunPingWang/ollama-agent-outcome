package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        var profileOpt = loadProfilePort.findById(command.id());
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(command.id().value());
        }

        com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile existingProfile = profileOpt.get();

        // Check duplicate email only if the provided email is different from current one or explicitly set
        String newEmailStr = command.email();
        boolean hasNewEmail = newEmailStr != null && !newEmailStr.isBlank();
        
        if (hasNewEmail && !newEmailStr.equals(existingProfile.getEmail().address())) {
            Optional<com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile> otherProfile = 
                loadProfilePort.findByEmail(newEmailStr);
            
            if (otherProfile.isPresent() && !otherProfile.get().getId().equals(command.id().value())) {
                throw new DuplicateEmailException(newEmailStr);
            }
        }

        DisplayName newDisplayName = DisplayName.of(command.displayName());
        Bio newBio = Bio.of(command.bio());
        
        existingProfile.update(newDisplayName, newBio);
        
        saveProfilePort.save(existingProfile);
    }
}
