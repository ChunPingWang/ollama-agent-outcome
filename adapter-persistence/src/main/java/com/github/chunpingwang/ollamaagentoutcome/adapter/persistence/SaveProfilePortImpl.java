package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Implementation of SaveProfilePort using Spring Data JPA.
 */
@Component
class SaveProfilePortImpl implements SaveProfilePort {

	private final ProfileRepository profileRepository;

	SaveProfilePortImpl(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Override
	public ProfileId save(Profile profile) {
		try {
			var entity = toEntity(profile);
			profileRepository.saveAndFlush(entity);
			return new ProfileId(entity.getId());
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateEmailException();
		}
	}

	static ProfileEntity toEntity(Profile profile) {
		var entity = new ProfileEntity(
				profile.id().value(),
				profile.name(),
				profile.email().address(),
				profile.age().years()
		);
		return entity;
	}
}
