package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

import org.junit.jupiter.api.Test;

class ProfileTest {

	private final ProfileId id = new ProfileId(UUID.randomUUID().toString());
	private final Name name = new Name("John Doe");
	private final Email email = new Email("john@example.com");
	private final Age age = new Age(30);

	@Test
	void should_create_profile_with_valid_data() {
		var profile = Profile.create(id, name, email, age);

		assertThat(profile.id()).isEqualTo(id);
		assertThat(profile.name()).isEqualTo(name);
		assertThat(profile.email()).isEqualTo(email);
		assertThat(profile.age()).isEqualTo(age);
	}

	@Test
	void should_update_name_successfully() {
		var newName = new Name("Jane Doe");
		var updatedProfile = profileWithNewName().update(newName, null, null);

		assertThat(updatedProfile.name()).isEqualTo(newName);
		assertThat(updatedProfile.email()).isEqualTo(email);
		assertThat(updatedProfile.age()).isEqualTo(age);
	}

	@Test
	void should_throw_when_updating_to_null_name() {
		assertThatThrownBy(() -> profileWithNewName().update(null, null, null))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("name cannot be null");
	}

	@Test
	void should_return_new_instance_on_update() {
		var newName = new Name("Jane Doe");
		var originalProfile = profileWithNewName();
		var updatedProfile = originalProfile.update(newName, null, null);

		assertThat(updatedProfile).isNotSameAs(originalProfile);
		assertThat(updatedProfile.name()).isEqualTo(newName);
	}

	private Profile profileWithNewName() {
		return Profile.create(id, name, email, age);
	}
}
