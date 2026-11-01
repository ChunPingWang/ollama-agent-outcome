package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for deleting a user profile by ID.
 * Verifies existence before deletion to ensure proper error handling (404).
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final DeleteProfilePort deleteProfilePort;

    /**
     * Constructs a DeleteProfileService with the given ports.
     *
     * @param findProfileByIdPort the port used to fetch profiles by ID
     * @param deleteProfilePort   the port used to remove profiles from storage
     */
    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, DeleteProfilePort deleteProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        UUID id = command.id();
        
        // Verify that the profile exists before attempting deletion
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> optionalProfile = findProfileByIdPort.findById(id);
        if (optionalProfile.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        // Perform deletion via outbound port
        deleteProfilePort.deleteById(id);
    }
}
