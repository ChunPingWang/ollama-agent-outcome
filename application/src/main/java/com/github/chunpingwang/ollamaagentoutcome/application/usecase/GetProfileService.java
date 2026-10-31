package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;

/**
 * 查詢個人資料的應用層服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

	private final LoadProfilePort loadProfilePort;

	public GetProfileService(LoadProfilePort loadProfilePort) {
		this.loadProfilePort = loadProfilePort;
	}

	@Override
	public Profile execute(String profileId) {
		return loadProfilePort.findById(profileId)
				.orElseThrow(() -> new ProfileNotFoundException(profileId));
	}
}
