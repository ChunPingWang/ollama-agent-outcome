package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
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
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

	@InjectMocks
	private DeleteProfileService deleteProfileService;

	@Mock
	private LoadProfilePort loadProfilePort;

	@Mock
	private RemoveProfilePort removeProfilePort;

	@Test
	void should_delete_profile_when_exists() {
		UUID id = UUID.randomUUID();
		var profileId = new ProfileId(id.toString());
		var existingProfile = new Profile(profileId, "John Doe", new Email("john@example.com"), new Age(30));

		when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
		doNothing().when(removeProfilePort).removeById(profileId);

		deleteProfileService.deleteById(profileId);

		verify(removeProfilePort).removeById(profileId);
	}

	@Test
	void should_throw_exception_when_profile_not_found() {
		UUID id = UUID.randomUUID();
		var profileId = new ProfileId(id.toString());

		when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> deleteProfileService.deleteById(profileId))
				.isInstanceOf(ProfileNotFoundException.class)
				.hasMessageContaining(id.toString());
	}
}
