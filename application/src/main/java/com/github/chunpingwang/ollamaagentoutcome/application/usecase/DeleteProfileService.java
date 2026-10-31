package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.UUID;

class DeleteProfileService implements DeleteProfileUseCase {
    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        if (loadProfilePort.findById(new ProfileId(UUID.fromString(command.id()))).isEmpty()) {
            throw new ProfileNotFoundException("profile not found: " + command.id());
        }
        deleteProfilePort.deleteById(new ProfileId(UUID.fromString(command.id())));
    }
}
