package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import com.github.chunpingwang.ollamaagentoutcome.application.dto.ProfileResponseDTO;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 查詢個人資料的使用情境實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    public GetProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    public ProfileResponseDTO execute(ProfileId id) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);

        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        Profile profile = profileOpt.get();
        
        return new ProfileResponseDTO(
                profile.getId().value(),
                profile.getEmail().address(),
                profile.getDisplayName() != null ? profile.getDisplayName().name() : null,
                profile.getBio() != null ? profile.getBio().content() : null
        );
    }
}
