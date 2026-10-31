package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;

/**
 * 刪除個人資料的使用情境實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfileByIdPort deleteProfileByIdPort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfileByIdPort deleteProfileByIdPort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
        this.deleteProfileByIdPort = Objects.requireNonNull(deleteProfileByIdPort);
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        UUID idValue = command.id();
        if (idValue == null) {
            throw new IllegalArgumentException("profile id cannot be null");
        }

        Profile profile = loadProfilePort.findById(new ProfileId(idValue))
                .orElseThrow(() -> new ProfileNotFoundException(idValue));

        // Port expects UUID, so extract the raw value from domain ID object
        deleteProfileByIdPort.deleteById(profile.id().value());
    }
}
