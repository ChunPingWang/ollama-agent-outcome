package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNullElse;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 更新個人資料的使用者情境服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = requiresNonNull(loadProfilePort, "loadProfilePort must not be null");
        this.saveProfilePort = requiresNonNull(saveProfilePort, "saveProfilePort must not be null");
    }

    @Override
    public void execute(UpdateProfileRequest request) {
        var profileOpt = loadProfilePort.findById(request.id());
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(request.id().value());
        }
        
        var profile = profileOpt.get();
        // Note: The domain method changeDisplayName might have validation logic inside. 
        // We pass the displayName from request directly. If it's blank/null and we want to allow clearing or keeping old?
        // Spec says update display name. Usually implies setting a new value. 
        // Let's assume DisplayName VO handles null/blank normalization.
        profile.changeDisplayName(request.displayName());
        
        saveProfilePort.save(profile);
    }
    
    private static <T> T requiresNonNull(T obj, String message) {
        java.util.Objects.requireNonNull(obj, message);
        return obj;
    }
}
