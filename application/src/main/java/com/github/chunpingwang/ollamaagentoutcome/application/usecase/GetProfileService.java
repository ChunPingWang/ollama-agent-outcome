package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 查詢個人資料的使用情境實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
    }

    @Override
    public ProfileResult execute(GetProfileQuery query) {
        UUID idValue = query.id();
        if (idValue == null) {
            throw new IllegalArgumentException("profile id cannot be null");
        }

        Profile profile = loadProfilePort.findById(new ProfileId(idValue))
                .orElseThrow(() -> new ProfileNotFoundException(idValue));

        return new ProfileResult(
                profile.id().value(),
                profile.name().value(),
                profile.email().value(),
                profile.bio() != null ? profile.bio().value() : ""
        );
    }
}
