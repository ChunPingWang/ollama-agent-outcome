package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.UUID;

/**
 * 建立個人資料的服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileRequest request) {
        validateEmail(request.email());
        
        Profile profile = Profile.create(
            new ProfileId(UUID.randomUUID().toString()),
            new Email(request.email()),
            new DisplayName(request.displayName())
        );
        
        // Port implementation is responsible for throwing DuplicateEmailException if constraint violated.
        // This service simply propagates it by not catching it.
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
