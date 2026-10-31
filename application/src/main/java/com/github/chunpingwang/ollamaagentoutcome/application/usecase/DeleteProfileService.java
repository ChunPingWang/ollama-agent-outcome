package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Service implementation for deleting a profile by ID.
 */
class DeleteProfileService implements DeleteProfileUseCase {

	private final LoadProfilePort loadProfilePort;
	private final RemoveProfilePort removeProfilePort;

	DeleteProfileService(LoadProfilePort loadProfilePort, RemoveProfilePort removeProfilePort) {
		this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
		this.removeProfilePort = Objects.requireNonNull(removeProfilePort);
	}

	@Override
	public void deleteById(ProfileId id) {
		Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> optionalProfile = loadProfilePort.findById(id);
		
		if (optionalProfile.isEmpty()) {
			throw new ProfileNotFoundException(id.value());
		}

		removeProfilePort.removeById(id);
	}
}
