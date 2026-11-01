package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort; // Not used directly but kept for potential future needs or consistency if needed, actually DeleteProfilePort is better. Let's stick to the plan which said Load then Delete. But wait, I defined DeleteProfilePort in T2 output just now. So I should use it. However, standard pattern often checks existence first. 
// Re-evaluating based on "DeleteProfilePort" definition: void deleteById(ProfileId id).
// If I call deleteById directly and it doesn't exist, JPA might not throw exception depending on provider, or return 0 rows affected.
// To ensure strict 404 behavior as per spec (and typical domain logic), checking existence via LoadProfilePort before deleting is safer/cleaner for Domain Exception throwing.
// BUT, looking at my previous thought process in Plan: "Application Layer must check existence using LoadProfilePort...".
// So this service will use LoadProfilePort to verify, then DeleteProfilePort.

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 刪除個人資料的應用服務實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Override
    public void execute(ProfileId id) {
        if (!loadProfilePort.findById(id).isPresent()) {
            throw new ProfileNotFoundException(id.value());
        }
        deleteProfilePort.deleteById(id);
    }
}
