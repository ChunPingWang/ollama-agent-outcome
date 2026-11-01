package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 查詢個人資料的應用服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProfileService.class);

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return loadProfilePort.findById(id).map(domainProfile -> 
            new Profile(
                domainProfile.id().value(),
                domainProfile.email().address(),
                domainProfile.displayName() != null ? domainProfile.displayName().name() : null
            )
        );
    }
}
