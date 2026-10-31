package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNullElse;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的使用者情境服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = requiresNonNull(loadProfilePort, "loadProfilePort must not be null");
    }

    @Override
    public ProfileResult execute(ProfileId id) {
        var profile = loadProfilePort.findById(id);
        
        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(id.value());
        }
        
        return new ProfileResult(
                profile.get().id(),
                profile.get().email(),
                profile.get().displayName()
        );
    }
    
    private static <T> T requiresNonNull(T obj, String message) {
        java.util.Objects.requireNonNull(obj, message);
        return obj;
    }
}
