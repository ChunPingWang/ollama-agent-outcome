package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 刪除個人資料的服務實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final DeleteProfileByIdPort deleteProfileByIdPort;

    DeleteProfileService(FindProfileByIdPort findProfileByIdPort, DeleteProfileByIdPort deleteProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.deleteProfileByIdPort = deleteProfileByIdPort;
    }

    @Override
    public void execute(ProfileId id) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(UUID.fromString(id.value()));
        }
        
        deleteProfileByIdPort.deleteById(id);
    }
}
