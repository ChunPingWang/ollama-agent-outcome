package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
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
class GetProfileServiceTest {

	@Mock
	private LoadProfilePort loadProfilePort;

	@InjectMocks
	private GetProfileService getProfileService;

	@Test
	void should_return_profile_when_found() {
		var id = new ProfileId(UUID.randomUUID().toString());
		var name = new Name("John Doe");
		var email = new Email("john@example.com");
		var age = new Age(30);
		var profile = Profile.create(id, name, email, age);

		when(loadProfilePort.findById(id.id())).thenReturn(Optional.of(profile));

		Profile result = getProfileService.execute(id.id());

		assertThat(result).isEqualTo(profile);
	}

	@Test
	void should_throw_exception_when_not_found() {
		String nonExistentId = UUID.randomUUID().toString();

		when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> getProfileService.execute(nonExistentId))
				.isInstanceOf(ProfileNotFoundException.class)
				.hasMessageContaining("profile not found with id: " + nonExistentId);
	}
}
