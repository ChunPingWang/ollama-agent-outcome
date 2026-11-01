package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 刪除個人資料的 Use Case 實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
        this.deleteProfilePort = Objects.requireNonNull(deleteProfilePort);
    }

    @Override
    public void execute(UUID id) {
        // 先檢查是否存在，若不存在則拋出例外
        if (loadProfilePort.findById(id).isEmpty()) {
            throw new ProfileNotFoundException("profile not found: " + id);
        }
        
        deleteProfilePort.deleteById(id);
    }
}
