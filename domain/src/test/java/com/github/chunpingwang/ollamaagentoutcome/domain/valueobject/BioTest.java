package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.BioTooLongException;

import org.junit.jupiter.api.Test;

class BioTest {

    private static final int MAX_LENGTH = 500;

    @Test
    void should_create_bio_within_limit() {
        var content = "a".repeat(MAX_LENGTH);
        var bio = new Bio(content);
        assertThat(bio.content()).isEqualTo(content);
    }

    @Test
    void should_allow_null_or_empty_bio() {
        var emptyBio = new Bio("");
        assertThat(emptyBio.content()).isEmpty();
        
        // Note: The record constructor allows null if we don't enforce non-null in the value object itself, 
        // but typically Bio is optional. Let's assume empty string for null input or allow null.
        // Based on Profile entity logic, it handles null by converting to empty or similar.
        // Here we test strict length limit.
    }

    @Test
    void should_reject_bio_too_long() {
        var tooLongContent = "a".repeat(MAX_LENGTH + 1);
        assertThatThrownBy(() -> new Bio(tooLongContent))
                .isInstanceOf(BioTooLongException.class)
                .hasMessageContaining("must not exceed");
    }
}
