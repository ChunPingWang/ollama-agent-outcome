package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(String email, String displayName) {
        Profile profile = Profile.create(
            new ProfileId(java.util.UUID.randomUUID()),
            new Email(email),
            new DisplayName(displayName)
        );
        saveProfilePort.save(profile);
    }
}
