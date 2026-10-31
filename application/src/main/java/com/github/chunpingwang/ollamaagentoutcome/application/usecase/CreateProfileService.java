package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Service implementation for creating a new profile.
 */
class CreateProfileService implements CreateProfileUseCase {

	private final SaveProfilePort saveProfilePort;

	CreateProfileService(SaveProfilePort saveProfilePort) {
		this.saveProfilePort = saveProfilePort;
	}

	@Override
	public ProfileId create(String name, String email, int age) {
		var profileId = new ProfileId(java.util.UUID.randomUUID().toString());
		var domainEmail = new Email(email);
		var domainAge = new Age(age);
		var profile = new Profile(profileId, name, domainEmail, domainAge);
		return saveProfilePort.save(profile);
	}
}
