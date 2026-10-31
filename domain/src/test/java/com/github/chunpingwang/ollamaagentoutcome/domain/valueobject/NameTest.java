package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

import org.junit.jupiter.api.Test;

class NameTest {

	@Test
	void should_create_valid_name() {
		var name = new Name("John Doe");
		assertThat(name.text()).isEqualTo("John Doe");
	}

	@Test
	void should_reject_null_name() {
		assertThatThrownBy(() -> new Name(null))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("name cannot be null");
	}

	@Test
	void should_reject_empty_name() {
		assertThatThrownBy(() -> new Name(""))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("name cannot be empty");
	}

	@Test
	void should_reject_too_long_name() {
		assertThatThrownBy(() -> new Name("a".repeat(256)))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("name length must be between 1 and 255");
	}
}
