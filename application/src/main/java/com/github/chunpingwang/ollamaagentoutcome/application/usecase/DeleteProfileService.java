package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;

/**
 * 刪除個人資料的應用層服務實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

	private final LoadProfilePort loadProfilePort;
	private final DeleteProfilePort deleteProfilePort;

	public DeleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
		this.loadProfilePort = loadProfilePort;
		this.deleteProfilePort = deleteProfilePort;
	}

	@Override
	public void execute(String profileId) {
		Optional<?> existingProfile = loadProfilePort.findById(profileId);
		if (existingProfile.isEmpty()) {
			throw new ProfileNotFoundException(profileId);
		}
		
		deleteProfilePort.deleteById(profileId);
	}
}
