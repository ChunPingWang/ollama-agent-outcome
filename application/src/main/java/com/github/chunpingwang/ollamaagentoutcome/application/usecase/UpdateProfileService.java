package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的使用案例實作。
 */
public class UpdateProfileService implements com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        Profile profile = findProfileByIdPort.findById(command.id())
            .orElseThrow(() -> new ProfileNotFoundException(command.id()));

        // 更新領域物件狀態
        profile.updateName(command.name());
        profile.updateEmail(command.email());

        // 儲存更新後的實體
        saveProfilePort.save(profile);
    }
}
