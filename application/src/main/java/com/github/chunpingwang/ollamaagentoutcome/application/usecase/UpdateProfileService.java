package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidAgeException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Service implementation for updating an existing profile.
 */
class UpdateProfileService implements UpdateProfileUseCase {

	private final LoadProfilePort loadProfilePort;
	private final SaveProfilePort saveProfilePort;

	UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
		this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
		this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
	}

	@Override
	public void update(ProfileId id, String name, String email, int age) {
		Optional<Profile> optionalProfile = loadProfilePort.findById(id);
		
		if (optionalProfile.isEmpty()) {
			throw new ProfileNotFoundException(id.value());
		}

		// Validate inputs first to fail fast before any DB operations if needed, 
		// though the domain entity's update method also handles validation.
        // We rely on Domain Entity's internal validation in this case as per TDD flow where tests expect propagation from update() call chain.
        
		Profile profile = optionalProfile.get();
		profile.update(name, email, age);
		
		saveProfilePort.save(profile);
	}
}
