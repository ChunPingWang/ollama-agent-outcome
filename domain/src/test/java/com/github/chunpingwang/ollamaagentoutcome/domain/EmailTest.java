package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @ParameterizedTest(name = "[{index}] should create valid email with address={0}")
    @ValueSource(strings = {"user@example.com", "test.user+tag@domain.co.uk"})
    void should_create_valid_email(String address) {
        // when
        Email email = new Email(address);

        // then
        assertThat(email.address()).isEqualTo(address);
    }

    @ParameterizedTest(name = "[{index}] should reject null or blank email")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid-email-format"})
    void should_reject_null_or_blank_email(String invalidAddress) {
        // expect
        assertThatThrownBy(() -> new Email(invalidAddress))
            .isInstanceOf(InvalidProfileException.class)
            .hasMessageContaining("email");
    }
}
