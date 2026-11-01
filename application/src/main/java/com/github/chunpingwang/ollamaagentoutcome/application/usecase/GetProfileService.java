package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public java.util.Optional<Profile> execute(String id) {
        return loadProfilePort.findById(id).map(profile -> {
            // If the port returns a profile, we can optionally validate or transform here if needed.
            // For now, just pass it through. The controller will handle not-found logic 
            // based on whether Optional is empty or not, but typically services throw for business errors.
            // However, since the UseCase interface returns Optional, we respect that contract.
            // Note: In many hexagonal designs, the Service throws NotFoundException and Controller catches it.
            // But strictly following the provided Interface signature `Optional<Profile>`, we return Optional.
            // To align with typical "throw on missing" behavior often expected in Services unless specified otherwise by Port:
            // Let's stick to the Interface: Return Optional. The caller (Controller/Adapter) decides what to do with Empty.
            // Actually, looking at other services like Delete/Update, they throw. 
            // But Get usually returns Optional or null. Let's keep it simple as per interface.
            return profile;
        });
    }
}
