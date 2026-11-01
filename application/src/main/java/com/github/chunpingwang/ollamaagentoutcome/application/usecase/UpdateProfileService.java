package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * Service implementation for updating an existing user profile.
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        if (findProfileByIdPort.findById(command.id()).isEmpty()) {
            throw new ProfileNotFoundException(command.id());
        }
        
        Email newEmail = new Email(command.email());
        DisplayName newDisplayName = new DisplayName(command.displayName());
        
        // Note: In a real scenario with optimistic locking or strict entity management, 
        // we might fetch the managed entity first to update it in place. 
        // However, based on current Port design (save takes domain object), 
        // and assuming the port handles merge logic or we pass the full updated state:
        // Since we don't have the old email here without fetching again, and spec says "update",
        // typically we need the existing entity. But for simplicity of this specific task fix,
        // we assume the Service constructs the update payload. 
        // To strictly follow "fetch then update" pattern implied by persistence traps:
        var profileOpt = findProfileByIdPort.findById(command.id());
        if (profileOpt.isEmpty()) {
             throw new ProfileNotFoundException(command.id());
        }
        Profile profile = profileOpt.get();
        profile.update(newEmail, newDisplayName);
        saveProfilePort.save(profile);
    }
}
