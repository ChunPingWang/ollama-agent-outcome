package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import java.util.UUID;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort, 
                                SaveProfilePort saveProfilePort, 
                                ExistsByEmailPort existsByEmailPort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public void execute(UUID id, String email, String displayName) {
        Profile profile = findProfileByIdPort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        boolean emailChanged = (email != null && !email.isBlank()) 
            && !profile.getEmail().getAddress().equals(email);
        
        if (emailChanged && existsByEmailPort.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        if (email != null && !email.isBlank()) {
            profile.updateEmail(email);
        }
        
        if (displayName != null) {
             profile.updateDisplayName(displayName);
        }

        saveProfilePort.save(profile);
    }
}
