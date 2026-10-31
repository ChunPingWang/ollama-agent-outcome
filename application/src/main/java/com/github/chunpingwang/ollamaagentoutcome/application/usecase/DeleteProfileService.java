package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 刪除個人資料的使用案例實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, DeleteProfilePort deleteProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(UUID id) {
        if (findProfileByIdPort.findById(id).isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        deleteProfilePort.deleteById(id);
    }
}
