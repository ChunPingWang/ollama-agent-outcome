package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidAgeException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

	@InjectMocks
	private UpdateProfileService updateProfileService;

	@Mock
	private LoadProfilePort loadProfilePort;

	@Mock
	private SaveProfilePort saveProfilePort;

	@Test
	void should_update_profile_when_exists() {
		UUID id = UUID.randomUUID();
		String oldName = "Old Name";
		String newName = "New Name";
		String emailStr = "new@example.com";
		int age = 30;

		var profileId = new ProfileId(id.toString());
		var existingProfile = new Profile(profileId, oldName, new Email("old@example.com"), new Age(25));

		when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
		
		// Fix: save() returns ProfileId (non-void), so use when().thenReturn() instead of doNothing()
		var savedId = new ProfileId("updated-id");
		when(saveProfilePort.save(any(Profile.class))).thenReturn(savedId);

		updateProfileService.update(profileId, newName, emailStr, age);

		verify(saveProfilePort).save(any(Profile.class));
	}

	@Test
	void should_throw_exception_when_profile_not_found() {
		UUID id = UUID.randomUUID();
		var profileId = new ProfileId(id.toString());

		when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> updateProfileService.update(profileId, "Name", "email@test.com", 25))
				.isInstanceOf(ProfileNotFoundException.class)
				.hasMessageContaining(id.toString());
	}

	@Test
	void should_propagate_duplicate_email_exception_from_save_port() {
		UUID id = UUID.randomUUID();
		var profileId = new ProfileId(id.toString());
		var existingProfile = new Profile(profileId, "Old Name", new Email("old@example.com"), new Age(25));

		when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
		when(saveProfilePort.save(any(Profile.class))).thenThrow(new DuplicateEmailException());

		assertThatThrownBy(() -> updateProfileService.update(profileId, "New Name", "duplicate@test.com", 30))
				.isInstanceOf(DuplicateEmailException.class)
				.hasMessage("Email address already exists");
	}
	
	@Test
	void should_propagate_invalid_age_exception_during_update_validation() {
		UUID id = UUID.randomUUID();
		var profileId = new ProfileId(id.toString());
		var existingProfile = new Profile(profileId, "Old Name", new Email("old@example.com"), new Age(25));

		when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));

		assertThatThrownBy(() -> updateProfileService.update(profileId, "Name", "email@test.com", -1))
				.isInstanceOf(InvalidAgeException.class);
	}
}
