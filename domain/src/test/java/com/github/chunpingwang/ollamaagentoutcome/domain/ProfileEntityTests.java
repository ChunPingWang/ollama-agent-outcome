package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileEntityTests {

	private final String validName = "John Doe";
	private final String validEmailStr = "john@example.com";
	private final int validAge = 30;

	@Test
	void should_create_profile_with_all_fields() {
		String idValue = UUID.randomUUID().toString();
		var profile = new Profile(new ProfileId(idValue), validName, new Email(validEmailStr), new Age(validAge));

		assertThat(profile.id().value()).isEqualTo(idValue);
		assertThat(profile.name()).isEqualTo(validName);
		assertThat(profile.email().address()).isEqualTo(validEmailStr);
		assertThat(profile.age().years()).isEqualTo(validAge);
	}

	@Test
	void should_update_name_successfully() {
		String idValue = UUID.randomUUID().toString();
		var email = new Email("old@example.com");
		var age = new Age(25);
		var profile = new Profile(new ProfileId(idValue), "Old Name", email, age);

		profile.update("New Name", "new@example.com", 26);

		assertThat(profile.name()).isEqualTo("New Name");
		assertThat(profile.email().address()).isEqualTo("new@example.com");
		assertThat(profile.age().years()).isEqualTo(26);
	}

	@Test
	void should_throw_invalid_age_exception_on_negative_age_during_update() {
		String idValue = UUID.randomUUID().toString();
		var profile = new Profile(new ProfileId(idValue), validName, new Email(validEmailStr), new Age(validAge));

		assertThatThrownBy(() -> profile.update("Updated Name", "updated@example.com", -5))
				.isInstanceOf(InvalidAgeException.class)
				.hasMessageContaining("must not be negative");
	}

	@Test
	void should_throw_invalid_email_format_exception_on_bad_email_during_update() {
		String idValue = UUID.randomUUID().toString();
		var profile = new Profile(new ProfileId(idValue), validName, new Email(validEmailStr), new Age(validAge));

		assertThatThrownBy(() -> profile.update("Updated Name", "bad-email", 30))
				.isInstanceOf(InvalidEmailFormatException.class);
	}

	@Test
	void should_allow_valid_update() {
		String idValue = UUID.randomUUID().toString();
		var email = new Email("old@example.com");
		var profile = new Profile(new ProfileId(idValue), "Old Name", email, new Age(25));

		profile.update("New Name", "new@example.com", 26);

		assertThat(profile.name()).isEqualTo("New Name");
		assertThat(profile.email().address()).isEqualTo("new@example.com");
		assertThat(profile.age().years()).isEqualTo(26);
	}
	
	@Test
	void should_not_change_id_after_creation() {
		String initialId = UUID.randomUUID().toString();
		var profile = new Profile(new ProfileId(initialId), validName, new Email(validEmailStr), new Age(validAge));
		
		profile.update("Changed Name", "changed@example.com", 31);
		
		assertThat(profile.id().value()).isEqualTo(initialId);
	}
}
