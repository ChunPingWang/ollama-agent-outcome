package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 刪除個人資料的 Use Case 實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = requireNonNull(loadProfilePort);
        this.deleteProfilePort = requireNonNull(deleteProfilePort);
    }

    @Override
    public void execute(ProfileId id) {
        if (loadProfilePort.findById(id).isEmpty()) {
            throw new ProfileNotFoundException("Profile not found with ID: " + id.getId());
        }

        deleteProfilePort.deleteById(id);
    }
}
