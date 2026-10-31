package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 查詢個人資料的使用案例實作。
 */
public class LoadProfileService implements LoadProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    public LoadProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        var optional = findProfileByIdPort.findById(id);
        if (!optional.isPresent()) {
            throw new ProfileNotFoundException(id);
        }
        return optional;
    }
}
