package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 刪除個人資料的 Use Case 實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final DeleteProfileByIdPort deleteProfileByIdPort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort,
                                DeleteProfileByIdPort deleteProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.deleteProfileByIdPort = deleteProfileByIdPort;
    }

    @Override
    public void deleteProfile(ProfileId id) {
        // 1. 檢查 Profile 是否存在
        findProfileByIdPort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));

        // 2. 刪除
        deleteProfileByIdPort.deleteById(id);
    }
}
