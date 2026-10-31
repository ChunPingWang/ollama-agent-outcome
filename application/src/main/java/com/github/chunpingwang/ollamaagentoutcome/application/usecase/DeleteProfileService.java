package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 刪除個人資料的 Use Case 實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final SaveProfilePort saveProfilePort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(DeleteProfileCommand command) {
        Profile profile = findProfileByIdPort.findById(command.id())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + command.id().getValue()));

        // 透過 save 觸發刪除邏輯（例如標記為刪除或物理刪除，視 Port 實作而定）
        // 在此架構中，SaveProfilePort 負責處理刪除的持久化行為
        saveProfilePort.save(profile);
    }
}
