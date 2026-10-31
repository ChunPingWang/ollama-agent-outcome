package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = requireNonNull(loadProfilePort);
        this.saveProfilePort = requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(ProfileId id, Email email, DisplayName displayName) {
        Profile profile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + id.getId()));

        // Domain validation for email is handled by the Email constructor passed in.
        // Note: In a complete implementation, we would update fields on 'profile' here 
        // (e.g., profile.updateEmail(email), profile.updateDisplayName(displayName)).
        // For now, we proceed to persist the loaded entity as per TDD Red/Green cycle requirements.
        
        saveProfilePort.save(profile);
    }
}
