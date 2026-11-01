package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileRequest request, String id) {
        validateEmail(request.email());
        
        var profileOpt = loadProfilePort.findById(id);
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        var profile = profileOpt.get();
        profile.update(new Email(request.email()), new DisplayName(request.displayName()));
        
        saveProfilePort.save(profile);
    }

    private void validateEmail(String email) {
        try {
            new Email(email);
        } catch (IllegalArgumentException e) {
            throw new InvalidEmailFormatException("Invalid email format: " + email);
        }
    }
}
