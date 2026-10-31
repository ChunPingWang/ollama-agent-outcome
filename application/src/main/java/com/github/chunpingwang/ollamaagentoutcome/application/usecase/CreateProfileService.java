package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.UUID.randomUUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import java.time.Instant;

/**
 * 建立個人資料的應用層服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(Name name, Email email) {
        if (loadProfilePort.existsByEmail(email)) {
            throw new DuplicateEmailException("email already exists");
        }
        
        Profile profile = new Profile(
                new ProfileId(randomUUID()),
                name,
                email,
                Instant.now()
        );
        saveProfilePort.save(profile);
    }
}
