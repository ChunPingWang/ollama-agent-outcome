package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的使用案例實作。
 */
public class LoadProfileService implements LoadProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    public LoadProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    public ProfileResult execute(ProfileId id) {
        return findProfileByIdPort.findById(id)
            .map(profile -> new ProfileResult(profile.name(), profile.email()))
            .orElseThrow(() -> new ProfileNotFoundException(id));
    }
}
