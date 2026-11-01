package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * 查詢個人資料的 Service 實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    public GetProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    // 修正：方法名稱必須與介面定義一致，介面宣告為 findById
    public Profile findById(ProfileId id) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found with id: " + id.value());
        }
        
        return profileOpt.get();
    }
}
