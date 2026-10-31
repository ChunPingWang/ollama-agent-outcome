package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.command.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 刪除個人資料的使用情境實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        UUID id = command.id();
        
        if (loadProfilePort.findById(new ProfileId(id)).isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        loadProfilePort.deleteById(new ProfileId(id));
    }
}
