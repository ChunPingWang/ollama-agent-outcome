package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import java.util.Objects;
import java.util.Optional;

/**
 * 查詢個人資料的 Use Case 實作。
 */
public class GetProfileService implements com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    /**
     * 建構子注入。
     *
     * @param loadProfilePort 載入 Port
     */
    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort, "loadProfilePort must not be null");
    }

    @Override
    public Optional<Profile> execute(ProfileId id) {
        return loadProfilePort.findById(id);
    }
}
