package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

import java.util.UUID;

/**
 * 建立個人資料的應用層服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

	private final SaveProfilePort saveProfilePort;

	public CreateProfileService(SaveProfilePort saveProfilePort) {
		this.saveProfilePort = saveProfilePort;
	}

	@Override
	public Profile execute(Name name, Email email, Age age) {
		var id = new ProfileId(UUID.randomUUID().toString());
		var profile = Profile.create(id, name, email, age);
		saveProfilePort.save(profile);
		return profile;
	}
}
