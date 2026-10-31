package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 查詢個人資料的 Use Case 實作。
 * 負責載入資料並處理找不到資料的情況。
 */
public class LoadProfileService implements LoadProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    /**
     * 建構子。
     *
     * @param loadProfilePort 載入個人資料的 Port
     */
    public LoadProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Profile loadProfile(ProfileId id) {
        return loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + id));
    }
}
