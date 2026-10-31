package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的 Use Case 實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    public GetProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    public Profile execute(GetProfileCommand command) {
        return findProfileByIdPort.findById(command.id())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + command.id().getValue()));
    }
}
