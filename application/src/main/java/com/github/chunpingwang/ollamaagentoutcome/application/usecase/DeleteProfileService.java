package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 刪除個人資料的應用服務實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfileByIdPort deleteProfileByIdPort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfileByIdPort deleteProfileByIdPort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfileByIdPort = deleteProfileByIdPort;
    }

    @Override
    public void execute(String profileId) {
        Optional<?> existing = loadProfilePort.findById(profileId);
        if (existing.isEmpty()) {
            throw new ProfileNotFoundException("找不到識別碼為 " + profileId + " 的個人資料");
        }
        
        deleteProfileByIdPort.deleteById(profileId);
    }
}
