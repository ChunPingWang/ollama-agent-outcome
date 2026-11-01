package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 查詢個人資料的服務實作。
 */
class GetProfileService implements GetProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    GetProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    public Profile execute(ProfileId id) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);
        
        return profileOpt.orElseThrow(() -> new ProfileNotFoundException(UUID.fromString(id.value())));
    }
}
