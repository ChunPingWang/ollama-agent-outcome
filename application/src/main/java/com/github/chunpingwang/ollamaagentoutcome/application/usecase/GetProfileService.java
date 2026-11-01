package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 查詢個人資料的應用服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Optional<Profile> execute(String profileId) {
        return loadProfilePort.findById(profileId);
    }
}
