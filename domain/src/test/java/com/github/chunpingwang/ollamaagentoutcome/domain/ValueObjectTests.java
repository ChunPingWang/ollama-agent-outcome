package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectTests {

	@Test
	void should_create_valid_profile_id() {
		String uuid = UUID.randomUUID().toString();
		var id = new ProfileId(uuid);
		assertThat(id.value()).isEqualTo(uuid);
	}

	@Test
	void should_reject_empty_profile_id() {
		assertThatThrownBy(() -> new ProfileId(""))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("ID cannot be empty");
	}

	@Test
	void should_create_valid_email() {
		var email = new Email("user@example.com");
		assertThat(email.address()).isEqualTo("user@example.com");
	}

	@Test
	void should_reject_invalid_email_format() {
		assertThatThrownBy(() -> new Email("invalid-email"))
				.isInstanceOf(InvalidEmailFormatException.class);
	}

	@Test
	void should_reject_null_email_address() {
		assertThatThrownBy(() -> new Email(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Address cannot be null");
	}

	@Test
	void should_create_valid_age() {
		var age = new Age(25);
		assertThat(age.years()).isEqualTo(25);
	}

	@Test
	void should_reject_negative_age() {
		assertThatThrownBy(() -> new Age(-1))
				.isInstanceOf(InvalidAgeException.class)
				.hasMessageContaining("must not be negative");
	}

	@Test
	void should_allow_zero_age() {
		var age = new Age(0);
		assertThat(age.years()).isZero();
	}
}
