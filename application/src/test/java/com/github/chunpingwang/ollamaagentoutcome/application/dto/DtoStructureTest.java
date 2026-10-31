package com.github.chunpingwang.ollamaagentoutcome.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DtoStructureTest {

    @Test
    void get_profile_result_should_contain_expected_fields() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        Name name = new Name("Test");
        Email email = new Email("test@example.com");

        // When
        GetProfileResult result = new GetProfileResult(id, name, email);

        // Then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.email()).isEqualTo(email);
    }
}
