package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileQuery;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResponse;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;

/**
 * 查詢個人資料的 Use Case 實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public ProfileResponse execute(GetProfileQuery query) {
        var profileOpt = loadProfilePort.findById(query.id());
        
        return profileOpt.map(profile -> new ProfileResponse(
            profile.getId().toString(),
            profile.getEmail().address(),
            profile.getDisplayName() != null ? profile.getDisplayName().name() : null,
            profile.getBio() != null ? profile.getBio().content() : null
        )).orElseThrow(() -> new ProfileNotFoundException(query.id().value()));
    }
}
