package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;

/**
 * 刪除個人資料的 Use Case 實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final RemoveProfilePort removeProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, RemoveProfilePort removeProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.removeProfilePort = removeProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile> profileOpt = 
            loadProfilePort.findById(command.id());
            
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(command.id().value());
        }

        removeProfilePort.deleteById(command.id());
    }
}
