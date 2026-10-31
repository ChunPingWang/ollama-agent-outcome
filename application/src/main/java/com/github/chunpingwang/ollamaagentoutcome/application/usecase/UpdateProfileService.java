package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;

/**
 * 更新個人資料的應用層服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

	private final LoadProfilePort loadProfilePort;
	private final SaveProfilePort saveProfilePort;

	public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
		this.loadProfilePort = loadProfilePort;
		this.saveProfilePort = saveProfilePort;
	}

	@Override
	public void execute(String profileId, Name name, Email email, Age age) {
		var existingProfile = loadProfilePort.findById(profileId);
		if (existingProfile.isEmpty()) {
			throw new ProfileNotFoundException(profileId);
		}
		
		var updatedProfile = existingProfile.get().update(name, email, age);
		saveProfilePort.save(updatedProfile);
	}
}
