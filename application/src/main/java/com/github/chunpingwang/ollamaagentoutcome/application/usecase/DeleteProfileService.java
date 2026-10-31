package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 刪除個人資料的 Use Case 實作。
 * 負責驗證資料存在性並執行刪除。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    /**
     * 建構子。
     *
     * @param loadProfilePort  載入個人資料的 Port
     * @param deleteProfilePort 刪除個人資料的 Port
     */
    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void deleteProfile(ProfileId id) {
        // 1. 驗證資料是否存在
        loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + id));

        // 2. 執行刪除
        deleteProfilePort.deleteById(id);
    }
}
