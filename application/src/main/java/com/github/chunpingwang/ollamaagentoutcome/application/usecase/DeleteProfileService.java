package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的使用者情境服務實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = requireNonNull(loadProfilePort, "loadProfilePort must not be null");
        this.deleteProfilePort = requireNonNull(deleteProfilePort, "deleteProfilePort must not be null");
    }

    @Override
    public void execute(ProfileId id) {
        Optional<?> profileOpt = loadProfilePort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id.value());
        }
        
        deleteProfilePort.deleteById(id);
    }
}
