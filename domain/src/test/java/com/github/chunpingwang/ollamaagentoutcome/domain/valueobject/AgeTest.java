package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

import org.junit.jupiter.api.Test;

class AgeTest {

	@Test
	void should_create_valid_age_zero() {
		var age = new Age(0);
		assertThat(age.years()).isEqualTo(0);
	}

	@Test
	void should_create_valid_age_one_hundred_fifty() {
		var age = new Age(150);
		assertThat(age.years()).isEqualTo(150);
	}

	@Test
	void should_reject_negative_age() {
		assertThatThrownBy(() -> new Age(-1))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("age must be between 0 and 150");
	}

	@Test
	void should_reject_over_150_age() {
		assertThatThrownBy(() -> new Age(151))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("age must be between 0 and 150");
	}
}
