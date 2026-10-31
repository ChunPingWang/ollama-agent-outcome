package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import java.util.Optional;

/**
 * 刪除個人資料的 Use Case 實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        ProfileId profileId = new ProfileId(command.profileId());
        Optional<?> existingProfile = loadProfilePort.findById(profileId);

        if (existingProfile.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found: " + command.profileId());
        }

        deleteProfilePort.deleteById(profileId);
    }
}
