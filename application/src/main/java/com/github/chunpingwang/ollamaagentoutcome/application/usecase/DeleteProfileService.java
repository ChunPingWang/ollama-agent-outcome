package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;

/**
 * 刪除個人資料的使用者情境實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(UUID id) {
        var profile = loadProfilePort.findById(id).orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + id));
        
        // Verify existence is sufficient for business logic validation before deletion
        deleteProfilePort.deleteById(id);
    }
}
