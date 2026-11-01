package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

import java.util.UUID;

/**
 * Service implementation for deleting a user profile by ID.
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, DeleteProfilePort deleteProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        UUID id = command.id();
        
        if (findProfileByIdPort.findById(id).isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        deleteProfilePort.deleteById(id);
    }
}
