package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

/**
 * Service implementation for deleting a profile by ID.
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(DeleteProfilePort deleteProfilePort) {
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(UUID id) {
        // Note: The port itself handles existence check and deletion logic
        deleteProfilePort.deleteById(id);
    }
}
