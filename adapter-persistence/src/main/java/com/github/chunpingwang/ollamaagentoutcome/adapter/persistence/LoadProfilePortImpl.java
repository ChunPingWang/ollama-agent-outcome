package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Implementation of LoadProfilePort using Spring Data JPA.
 */
@Component
class LoadProfilePortImpl implements LoadProfilePort {

	private final ProfileRepository profileRepository;

	LoadProfilePortImpl(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Override
	public Optional<Profile> findById(ProfileId id) {
		return profileRepository.findById(id.value())
				.map(this::toDomain);
	}

	Profile toDomain(ProfileEntity entity) {
		var email = new com.github.chunpingwang.ollamaagentoutcome.domain.Email(entity.getEmail());
		var age = new com.github.chunpingwang.ollamaagentoutcome.domain.Age(entity.getAge());
		return new Profile(new ProfileId(entity.getId()), entity.getName(), email, age);
	}
}
