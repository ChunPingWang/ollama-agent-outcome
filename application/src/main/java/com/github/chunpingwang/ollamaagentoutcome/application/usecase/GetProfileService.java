package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的應用服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public ProfileResult execute(ProfileId id) {
        return loadProfilePort.findById(id)
                .map(this::toResult)
                .orElseThrow(() -> new ProfileNotFoundException(id.value()));
    }

    private ProfileResult toResult(Profile profile) {
        Email email = profile.email();
        DisplayName displayName = profile.displayName();
        
        // We need a way to get String values from VOs for the Result DTO if it expects Strings, 
        // or we keep VOs in the result? The plan said: "ProfileResult: record { ProfileId id, Email email, DisplayName displayName }"
        // But usually Web layer needs strings. Let's assume ProfileResult contains the Domain Objects or their string representations.
        // Given CreateProfileRequest used VOs (Email, DisplayName), let's stick to using VOs in Application Layer contracts where possible,
        // but wait, CreateProfileRequest had `Email` and `DisplayName`. So ProfileResult should probably have them too.
        // However, looking at existing files: CreateProfileRequest uses `com.github.chunpingwang.ollamaagentoutcome.domain.Email`.
        // So I will use domain objects in the Result as well to maintain purity of application port contract.
        
        return new ProfileResult(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(profile.id()),
            profile.email(),
            profile.displayName()
        );
    }
}
