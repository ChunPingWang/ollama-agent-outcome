package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的應用層服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    /**
     * 根據識別碼查詢個人資料。若不存在則拋出 ProfileNotFoundException。
     */
    @Override
    public java.util.Optional<Profile> findById(String id) {
        var optionalProfile = loadProfilePort.findById(id);
        if (optionalProfile.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        return optionalProfile;
    }
}
