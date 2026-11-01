package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.UUID;

/**
 * 新增個人資料的使用案例服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public void execute(CreateProfileRequest request) {
        Email email = new Email(request.email().value());
        DisplayName displayName = request.displayName() != null ? new DisplayName(request.displayName().value()) : null;

        if (existsByEmailPort.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        Profile profile = new Profile(UUID.randomUUID(), email, displayName);
        saveProfilePort.save(profile);
    }
}
