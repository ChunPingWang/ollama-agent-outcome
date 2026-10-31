package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的使用案例實作類別。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetProfileResponse get(UUID id) {
        var profile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        return new GetProfileResponse(
                UUID.fromString(profile.id().value()),
                profile.email().address(),
                profile.displayName().name()
        );
    }
}
