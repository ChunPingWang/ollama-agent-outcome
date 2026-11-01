package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Service implementation for DeleteProfileUseCase.
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final RemoveProfilePort removeProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, RemoveProfilePort removeProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.removeProfilePort = removeProfilePort;    }

    @Override
    public void execute(DeleteProfileCommand command) {
        Optional<Profile> existing = loadProfilePort.findById(command.id());
        
        if (existing.isEmpty()) {
            throw new ProfileNotFoundException(command.id());
        }
        
        removeProfilePort.deleteById(command.id());
    }
}
