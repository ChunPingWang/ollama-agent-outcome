package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;

/**
 * 查詢個人資料的使用案例服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public ProfileResult execute(ProfileId id) {
        Optional<Profile> profileOpt = loadProfilePort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id.value());
        }

        Profile profile = profileOpt.get();
        Email email = profile.email();
        DisplayName displayName = profile.displayName();

        return new ProfileResult(
                profile.id(),
                new Email(email.value()),
                displayName != null ? new DisplayName(displayName.value()) : null
        );
    }
}
