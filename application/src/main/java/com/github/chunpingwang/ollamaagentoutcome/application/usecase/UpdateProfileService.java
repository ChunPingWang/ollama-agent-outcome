package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的應用層服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    /**
     * 執行更新個人資料的業務邏輯。
     */
    @Override
    public void execute(UpdateRequest request, String id) {
        var profile = loadProfilePort.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
        
        Email email = new Email(request.email());
        DisplayName displayName = (request.displayName() != null && !request.displayName().isBlank()) 
                ? new DisplayName(request.displayName()) 
                : new DisplayName("");
                
        profile.update(email, displayName);
        saveProfilePort.save(profile);
    }
}
