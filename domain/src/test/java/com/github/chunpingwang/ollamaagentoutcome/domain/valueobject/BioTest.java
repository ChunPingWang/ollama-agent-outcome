package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.BioTooLongException;

class BioTest {

    private static final int MAX_LENGTH = 500;

    @Test
    void should_create_valid_bio() {
        String content = "A short bio.";
        Bio bio = new Bio(content);
        assertThat(bio.content()).isEqualTo(content);
    }

    @Test
    void should_allow_null_bio() {
        Bio bio = new Bio(null);
        assertThat(bio.content()).isNull();
    }

    @Test
    void should_reject_too_long_bio() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_LENGTH + 1; i++) {
            sb.append('a');
        }
        assertThatThrownBy(() -> new Bio(sb.toString()))
                .isInstanceOf(BioTooLongException.class)
                .hasMessageContaining("Bio length");
    }

    @Test
    void should_accept_max_length_bio() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_LENGTH; i++) {
            sb.append('a');
        }
        Bio bio = new Bio(sb.toString());
        assertThat(bio.content().length()).isEqualTo(MAX_LENGTH);
    }
}
