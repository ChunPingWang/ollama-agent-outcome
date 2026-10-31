package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileRequest request) {
        String email = request.email();
        if (existsByEmailPort.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        ProfileId id = new ProfileId(java.util.UUID.randomUUID().toString());
        Email emailValue = new Email(email);
        Name name = new Name(request.name());
        Age age = new Age(request.age());

        Profile profile = new Profile(id, emailValue, name, age);
        saveProfilePort.save(profile);
    }
}
