package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 查詢個人資料的應用層服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Optional<GetProfileUseCase.Profile> findById(String id) {
        return loadProfilePort.findById(id).map(profile -> 
            new GetProfileUseCase.Profile(
                profile.id().value(),
                profile.email().address(),
                profile.displayName() != null ? profile.displayName().name() : null
            )
        );
    }
}
