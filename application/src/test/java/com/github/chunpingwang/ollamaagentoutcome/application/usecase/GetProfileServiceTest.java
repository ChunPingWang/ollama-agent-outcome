package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

	@InjectMocks
	private GetProfileService getProfileService;

	@Mock
	private LoadProfilePort loadProfilePort;

	@Test
	void should_return_profile_when_exists() {
		UUID id = UUID.randomUUID();
		String name = "John Doe";
		String email = "john@example.com";
		int age = 30;
		
		var profileId = new ProfileId(id.toString());
		var expectedProfile = new Profile(profileId, name, new com.github.chunpingwang.ollamaagentoutcome.domain.Email(email), new com.github.chunpingwang.ollamaagentoutcome.domain.Age(age));

		when(loadProfilePort.findById(org.mockito.ArgumentMatchers.any(ProfileId.class))).thenReturn(Optional.of(expectedProfile));

		Optional<Profile> result = getProfileService.getById(profileId);

		assertThat(result).isPresent().get().satisfies(p -> {
			assertThat(p.id()).isEqualTo(profileId);
			assertThat(p.name()).isEqualTo(name);
			assertThat(p.email().address()).isEqualTo(email);
			assertThat(p.age().years()).isEqualTo(age);
		});
	}

	@Test
	void should_return_empty_optional_when_not_found() {
		UUID id = UUID.randomUUID();
		var profileId = new ProfileId(id.toString());

		when(loadProfilePort.findById(org.mockito.ArgumentMatchers.any(ProfileId.class))).thenReturn(Optional.empty());

		Optional<Profile> result = getProfileService.getById(profileId);

		assertThat(result).isEmpty();
	}
}
