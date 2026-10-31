package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

import org.junit.jupiter.api.Test;

class EmailTest {

	@Test
	void should_create_valid_email() {
		var email = new Email("user@example.com");
		assertThat(email.address()).isEqualTo("user@example.com");
	}

	@Test
	void should_reject_null_email() {
		assertThatThrownBy(() -> new Email(null))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("email cannot be null");
	}

	@Test
	void should_reject_empty_email() {
		assertThatThrownBy(() -> new Email(""))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("email cannot be empty");
	}

	@Test
	void should_reject_invalid_format() {
		assertThatThrownBy(() -> new Email("invalid-email"))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("invalid email format");
	}
}
