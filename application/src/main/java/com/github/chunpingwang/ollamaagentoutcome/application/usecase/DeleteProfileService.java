package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import java.util.UUID;

/**
 * 刪除個人資料的 Use Case 實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final DeleteProfileByIdPort deleteProfileByIdPort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, DeleteProfileByIdPort deleteProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.deleteProfileByIdPort = deleteProfileByIdPort;
    }

    @Override
    public void execute(UUID id) {
        if (!findProfileByIdPort.findById(id).isPresent()) {
            throw new ProfileNotFoundException(id);
        }
        
        deleteProfileByIdPort.deleteById(id);
    }
}
