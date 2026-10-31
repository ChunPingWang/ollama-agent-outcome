package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNullElse;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的使用者情境服務實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.loadProfilePort = requiresNonNull(loadProfilePort, "loadProfilePort must not be null");
        this.deleteProfilePort = requiresNonNull(deleteProfilePort, "deleteProfilePort must not be null");
    }

    @Override
    public void execute(ProfileId id) {
        var profile = loadProfilePort.findById(id);
        
        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(id.value());
        }
        
        deleteProfilePort.deleteById(id);
    }
    
    private static <T> T requiresNonNull(T obj, String message) {
        java.util.Objects.requireNonNull(obj, message);
        return obj;
    }
}
