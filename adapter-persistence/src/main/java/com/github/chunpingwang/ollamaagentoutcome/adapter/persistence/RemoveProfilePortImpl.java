package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Implementation of RemoveProfilePort using Spring Data JPA.
 */
@Component
class RemoveProfilePortImpl implements RemoveProfilePort {

	private final ProfileRepository profileRepository;

	RemoveProfilePortImpl(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Override
	public void removeById(ProfileId id) {
		if (!profileRepository.existsById(id.value())) {
			throw new ProfileNotFoundException(id.value());
		}
		profileRepository.deleteById(id.value());
		profileRepository.flush();
	}
}
