package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 刪除個人資料的 Service 實作。
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
    public void execute(ProfileId id) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found with id: " + id.value());
        }
        
        deleteProfileByIdPort.deleteById(id);
    }
}
