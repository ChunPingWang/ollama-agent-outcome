package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

	@InjectMocks
	private CreateProfileService createProfileService;

	@Mock
	private SaveProfilePort saveProfilePort;

	@Test
	void should_create_profile_and_return_id() {
		String name = "John Doe";
		String email = "john@example.com";
		int age = 30;
		UUID expectedUuid = UUID.randomUUID();
		ProfileId savedId = new ProfileId(expectedUuid.toString());

		when(saveProfilePort.save(any(Profile.class))).thenReturn(savedId);

		var result = createProfileService.create(name, email, age);

		assertThat(result).isEqualTo(savedId);
		verify(saveProfilePort).save(any(Profile.class));
	}

	@Test
	void should_throw_duplicate_email_exception_when_save_fails() {
		String name = "Jane Doe";
		String email = "jane@example.com";
		int age = 25;

		when(saveProfilePort.save(any(Profile.class)))
				.thenThrow(new DuplicateEmailException());

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> createProfileService.create(name, email, age))
				.isInstanceOf(DuplicateEmailException.class)
				.hasMessage("Email address already exists");
	}
}
