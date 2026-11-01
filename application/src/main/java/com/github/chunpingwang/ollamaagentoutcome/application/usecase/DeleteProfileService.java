package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Service implementation for deleting a user profile by ID.
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(DeleteProfilePort deleteProfilePort) {
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(ProfileId id) {
        if (!deleteProfilePort.existsById(id.id())) {
            throw new ProfileNotFoundException(id.id());
        }
        
        deleteProfilePort.deleteById(id.id());
    }
}
