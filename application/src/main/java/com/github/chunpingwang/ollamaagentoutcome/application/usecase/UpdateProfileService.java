package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的使用案例實作類別。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, 
                                ExistsByEmailPort existsByEmailPort, 
                                SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(UpdateProfileRequest request) {
        var targetId = request.id();

        var profile = loadProfilePort.findById(targetId)
                .orElseThrow(() -> new ProfileNotFoundException(targetId));

        var newEmail = new Email(request.email());

        // Check uniqueness only if email changed
        if (!profile.email().address().equals(newEmail.address())) {
            if (existsByEmailPort.existsByEmail(newEmail.address())) {
                throw new DuplicateEmailException("電子郵件已存在");
            }
        }

        var newDisplayName = new DisplayName(request.displayName());
        profile.updateDisplayName(newDisplayName);

        saveProfilePort.save(profile);
    }
}
