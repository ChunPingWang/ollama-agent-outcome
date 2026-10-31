package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

	@Mock
	private LoadProfilePort loadProfilePort;

	@Mock
	private SaveProfilePort saveProfilePort;

	@InjectMocks
	private UpdateProfileService updateProfileService;

	@Test
	void should_update_profile_successfully() {
		var id = new ProfileId(UUID.randomUUID().toString());
		var oldName = new Name("Old Name");
		var email = new Email("old@example.com");
		var age = new Age(30);
		var profile = Profile.create(id, oldName, email, age);

		when(loadProfilePort.findById(id.id())).thenReturn(Optional.of(profile));
		doNothing().when(saveProfilePort).save(any(Profile.class));

		var newName = new Name("New Name");
		var newEmail = new Email("new@example.com");
		var newAge = new Age(31);

		updateProfileService.execute(id.id(), newName, newEmail, newAge);

		verify(saveProfilePort).save(any(Profile.class));
	}

	@Test
	void should_throw_exception_when_not_found() {
		String nonExistentId = UUID.randomUUID().toString();

		when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> updateProfileService.execute(nonExistentId, new Name("Test"), new Email("test@test.com"), new Age(25)))
				.isInstanceOf(ProfileNotFoundException.class)
				.hasMessageContaining("profile not found with id: " + nonExistentId);
	}

	@Test
	void should_propagate_duplicate_email_exception() {
		var id = new ProfileId(UUID.randomUUID().toString());
		var name = new Name("Existing User");
		var email = new Email("existing@example.com");
		var age = new Age(30);
		var profile = Profile.create(id, name, email, age);

		when(loadProfilePort.findById(id.id())).thenReturn(Optional.of(profile));
		doThrow(new DuplicateEmailException())
				.when(saveProfilePort)
				.save(any(Profile.class));

		assertThatThrownBy(() -> updateProfileService.execute(id.id(), name, email, age))
				.isInstanceOf(DuplicateEmailException.class)
				.hasMessageContaining("email already exists");
	}
}
