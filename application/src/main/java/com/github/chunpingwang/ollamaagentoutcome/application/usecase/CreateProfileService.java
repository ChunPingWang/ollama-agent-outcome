package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 新增個人資料的使用者情境服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.saveProfilePort = requireNonNull(saveProfilePort, "saveProfilePort must not be null");
        this.existsByEmailPort = requireNonNull(existsByEmailPort, "existsByEmailPort must not be null");
    }

    @Override
    public void execute(CreateProfileRequest request) {
        var email = request.email();

        if (existsByEmailPort.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        var id = new ProfileId(java.util.UUID.randomUUID());
        var profile = new Profile(id, email, request.displayName());
        saveProfilePort.save(profile);
    }
}
