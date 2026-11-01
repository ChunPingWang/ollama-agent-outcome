package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 建立個人資料的應用服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public Profile execute(CreateProfileCommand command) {
        if (existsByEmailPort.existsByEmail(command.email())) {
            throw new DuplicateEmailException("電子郵件已存在: " + command.email());
        }

        String id = UUID.randomUUID().toString();
        Email email = new Email(command.email());
        DisplayName displayName = new DisplayName(command.displayName());
        
        return saveProfilePort.save(new Profile(id, email, displayName));
    }
}
