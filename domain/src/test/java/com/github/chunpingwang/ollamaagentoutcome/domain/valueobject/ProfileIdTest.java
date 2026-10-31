package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

import org.junit.jupiter.api.Test;

class ProfileIdTest {

	@Test
	void should_create_valid_uuid_id() {
		var uuid = UUID.randomUUID();
		var id = new ProfileId(uuid.toString());
		assertThat(id.id()).isEqualTo(uuid.toString());
	}

	@Test
	void should_reject_null_id() {
		assertThatThrownBy(() -> new ProfileId(null))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("id cannot be null");
	}

	@Test
	void should_reject_invalid_uuid_format() {
		assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
				.isInstanceOf(InvalidInputException.class)
				.hasMessageContaining("invalid profile id format");
	}
}
