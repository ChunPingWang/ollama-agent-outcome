package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的應用層服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public GetProfileResponse findById(String id) {
        return loadProfilePort.findById(id).map(domainEntity -> 
            new GetProfileResponse(
                domainEntity.id().value(),
                domainEntity.email().address(),
                domainEntity.displayName() != null ? domainEntity.displayName().name() : null
            )
        ).orElseThrow(() -> new ProfileNotFoundException("profile not found with id: " + id));
    }
}
