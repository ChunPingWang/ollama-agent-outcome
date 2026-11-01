package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的業務邏輯實作 (Use Case Implementation)。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final RemoveProfilePort removeProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, RemoveProfilePort removeProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.removeProfilePort = removeProfilePort;
    }

    @Override
    public void execute(ProfileId id) {
        if (!loadProfilePort.findById(id).isPresent()) {
            throw new ProfileNotFoundException("找不到識別碼為 " + id.toString() + " 的個人資料");
        }
        
        removeProfilePort.deleteById(id);
    }
}
