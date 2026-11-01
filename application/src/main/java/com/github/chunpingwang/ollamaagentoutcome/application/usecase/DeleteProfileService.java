package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 刪除個人資料的應用層服務實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    /**
     * 執行刪除個人資料的業務流程。
     */
    @Override
    public void execute(String id) {
        if (loadProfilePort.findById(id).isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        deleteProfilePort.deleteById(id);
    }
}
