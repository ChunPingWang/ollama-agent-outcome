package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
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
class CreateProfileServiceTest {

	@Mock
	private SaveProfilePort saveProfilePort;

	@InjectMocks
	private CreateProfileService createProfileService;

	@Test
	void should_create_profile_and_save() {
		var name = new Name("John Doe");
		var email = new Email("john@example.com");
		var age = new Age(30);

		Profile result = createProfileService.execute(name, email, age);

		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo(name);
		assertThat(result.email()).isEqualTo(email);
		assertThat(result.age()).isEqualTo(age);
		
		verify(saveProfilePort).save(any(Profile.class));
	}

	@Test
	void should_propagate_exception_from_port() {
		var name = new Name("Jane Doe");
		var email = new Email("jane@example.com");
		var age = new Age(25);

		doThrow(new DuplicateEmailException())
				.when(saveProfilePort)
				.save(any(Profile.class));

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> createProfileService.execute(name, email, age))
				.isInstanceOf(DuplicateEmailException.class)
				.hasMessageContaining("email already exists");
	}
}
