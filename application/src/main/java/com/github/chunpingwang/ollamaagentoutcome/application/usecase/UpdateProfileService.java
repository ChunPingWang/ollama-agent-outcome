package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
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
    public void update(ProfileId id, String name, String email) {
        Profile existingProfile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
        
        existingProfile.updateName(name);
        existingProfile.updateEmail(new Email(email));
        
        saveProfilePort.save(existingProfile);
    }
}
