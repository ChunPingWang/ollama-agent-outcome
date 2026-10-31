package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Service implementation for retrieving an existing profile by ID.
 */
class GetProfileService implements GetProfileUseCase {

	private final LoadProfilePort loadProfilePort;

	GetProfileService(LoadProfilePort loadProfilePort) {
		this.loadProfilePort = loadProfilePort;
	}

	@Override
	public Optional<Profile> getById(ProfileId id) {
		return loadProfilePort.findById(id);
	}
}
