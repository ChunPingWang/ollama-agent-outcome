package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 刪除個人資料的 Use Case 實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort, "loadProfilePort cannot be null");
        this.deleteProfilePort = Objects.requireNonNull(deleteProfilePort, "deleteProfilePort cannot be null");
    }

    @Override
    public void execute(UUID id) {
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> profileOpt = loadProfilePort.findById(new ProfileId(id));
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        deleteProfilePort.deleteById(new ProfileId(id));
    }
}
