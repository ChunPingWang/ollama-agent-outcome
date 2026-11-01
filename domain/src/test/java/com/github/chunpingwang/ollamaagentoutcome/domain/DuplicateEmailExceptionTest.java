package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DuplicateEmailExceptionTest {

    private final String validId = UUID.randomUUID().toString();
    private final Email email = new Email("dup@example.com");

    @Test
    void should_contain_correct_message_and_profile_id() {
        DuplicateEmailException ex = new DuplicateEmailException(new ProfileId(validId), email);
        
        assertThat(ex.getMessage())
                .contains("already exists")
                .contains("dup@example.com");
        assertThat(ex.getProfileId()).isEqualTo(new ProfileId(validId));
        assertThat(ex.getEmail()).isEqualTo(email);
    }
}
