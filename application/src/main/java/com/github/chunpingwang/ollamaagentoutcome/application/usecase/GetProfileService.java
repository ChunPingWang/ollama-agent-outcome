package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的使用者情境服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = requireNonNull(loadProfilePort, "loadProfilePort must not be null");
    }

    @Override
    public ProfileResult execute(ProfileId id) {
        Optional<?> profileOpt = loadProfilePort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id.value());
        }
        
        var profile = (com.github.chunpingwang.ollamaagentoutcome.domain.Profile) profileOpt.get();
        
        return new ProfileResult(
                profile.id(),
                profile.email(),
                profile.displayName()
        );
    }
}
