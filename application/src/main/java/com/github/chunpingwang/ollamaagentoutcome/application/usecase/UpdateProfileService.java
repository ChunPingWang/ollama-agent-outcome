package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 更新個人資料的使用者情境服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = requireNonNull(loadProfilePort, "loadProfilePort must not be null");
        this.saveProfilePort = requireNonNull(saveProfilePort, "saveProfilePort must not be null");
    }

    @Override
    public void execute(UpdateProfileRequest request) {
        Optional<?> profileOpt = loadProfilePort.findById(request.id());
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(request.id().value());
        }
        
        var profile = (com.github.chunpingwang.ollamaagentoutcome.domain.Profile) profileOpt.get();
        profile.changeDisplayName(request.displayName());
        
        saveProfilePort.save(profile);
    }
}
