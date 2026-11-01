package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的 Use Case 實作。
 */
class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadPort;

    public GetProfileService(LoadProfilePort loadPort) {
        this.loadPort = requireNonNull(loadPort, "loadPort 不能為 null");
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> execute(String id) {
        return loadPort.findById(requireNonNull(id, "id 不能為 null"));
    }
}
