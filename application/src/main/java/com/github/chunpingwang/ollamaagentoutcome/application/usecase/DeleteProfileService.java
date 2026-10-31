package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 刪除個人資料的使用情境實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final RemoveProfilePort removeProfilePort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, RemoveProfilePort removeProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.removeProfilePort = removeProfilePort;
    }

    @Override
    public void execute(ProfileId id) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);

        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        removeProfilePort.remove(id);
    }
}
