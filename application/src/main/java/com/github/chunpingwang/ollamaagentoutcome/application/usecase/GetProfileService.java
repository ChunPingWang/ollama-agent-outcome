package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 查詢個人資料的 Use Case 實作。
 */
class GetProfileService implements com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Optional<Profile> execute(UUID id) {
        return loadProfilePort.findById(id);
    }
}
