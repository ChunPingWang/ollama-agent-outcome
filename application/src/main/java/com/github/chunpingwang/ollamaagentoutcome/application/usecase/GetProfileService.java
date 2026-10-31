package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.dto.GetProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;

/**
 * 查詢個人資料的應用層服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public GetProfileResult execute(ProfileId id) {
        return loadProfilePort.findById(id).map(profile -> new GetProfileResult(
                profile.getId(),
                profile.getName(),
                profile.getEmail()
        )).orElseThrow(() -> new ProfileNotFoundException(id));
    }
}
