package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的 Use Case 實作。
 */
class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadPort;
    private final SaveProfilePort savePort;

    public UpdateProfileService(LoadProfilePort loadPort, SaveProfilePort savePort) {
        this.loadPort = requireNonNull(loadPort, "loadPort 不能為 null");
        this.savePort = requireNonNull(savePort, "savePort 不能為 null");
    }

    @Override
    public void execute(UpdateProfileRequest request, String id) {
        var profile = loadPort.findById(requireNonNull(id, "id 不能為 null"))
            .orElseThrow(() -> new ProfileNotFoundException(id));
        
        Email email = new Email(request.email());
        DisplayName displayName = new DisplayName(request.displayName());
        
        profile.update(email, displayName);
        savePort.save(profile);
    }
}
