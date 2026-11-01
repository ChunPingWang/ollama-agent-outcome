package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 新增個人資料的 Use Case 實作。
 */
class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort savePort;

    public CreateProfileService(SaveProfilePort savePort) {
        this.savePort = requireNonNull(savePort, "savePort 不能為 null");
    }

    @Override
    public void execute(CreateProfileRequest request) {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID().toString());
        Email email = new Email(request.email());
        DisplayName displayName = new DisplayName(request.displayName());
        
        Profile profile = Profile.create(id, email, displayName);
        savePort.save(profile);
    }
}
