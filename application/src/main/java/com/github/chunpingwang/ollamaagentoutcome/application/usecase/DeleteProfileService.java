package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 刪除個人資料的 Use Case 實作。
 */
class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadPort;
    private final DeleteProfilePort deletePort;

    public DeleteProfileService(LoadProfilePort loadPort, DeleteProfilePort deletePort) {
        this.loadPort = requireNonNull(loadPort, "loadPort 不能為 null");
        this.deletePort = requireNonNull(deletePort, "deletePort 不能為 null");
    }

    @Override
    public void execute(String id) {
        var profile = loadPort.findById(requireNonNull(id, "id 不能為 null"))
            .orElseThrow(() -> new ProfileNotFoundException(id));
        
        deletePort.deleteById(id);
    }
}
