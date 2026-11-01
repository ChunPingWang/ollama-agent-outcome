package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 刪除個人資料的 Use Case 實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    /**
     * 建構子注入。
     *
     * @param loadProfilePort   載入 Port
     * @param deleteProfilePort 刪除 Port
     */
    DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort, "loadProfilePort must not be null");
        this.deleteProfilePort = Objects.requireNonNull(deleteProfilePort, "deleteProfilePort must not be null");
    }

    @Override
    public void execute(UUID id) {
        ProfileId profileId = new ProfileId(id);
        
        Optional<?> existingOpt = loadProfilePort.findById(profileId);
        if (existingOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        deleteProfilePort.deleteById(profileId);
    }
}
