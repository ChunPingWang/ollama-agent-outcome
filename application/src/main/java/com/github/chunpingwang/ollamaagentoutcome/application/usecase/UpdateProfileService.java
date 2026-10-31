package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public GetProfileUseCase.ProfileResponse update(String profileId, UpdateProfileRequest request) {
        Profile existingProfile = loadProfilePort.findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));

        Profile updatedProfile = existingProfile.update(
            new Name(request.name()),
            new Bio(request.bio())
        );

        saveProfilePort.save(updatedProfile);

        return new GetProfileUseCase.ProfileResponse(
            updatedProfile.id().value(),
            updatedProfile.email().value(),
            updatedProfile.name().value(),
            updatedProfile.bio().value()
        );
    }
}
