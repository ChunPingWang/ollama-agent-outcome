package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidNameException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValueObjectValidationTest {

    @Test
    void should_throw_when_name_is_blank() {
        assertThatThrownBy(() -> new Name(""))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void should_throw_when_email_is_invalid_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_create_valid_profile_id() {
        UUID uuid = UUID.randomUUID();
        var profileId = new ProfileId(uuid);
        assertThat(profileId.value()).isEqualTo(uuid);
    }
}
