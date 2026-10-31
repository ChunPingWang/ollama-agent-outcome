package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProfileIdTest {

    @ParameterizedTest(name = "[{index}] id=[{0}] should be valid")
    @MethodSource("validIds")
    void should_create_valid_profile_id(String input) {
        var profileId = new ProfileId(input);
        assertThat(profileId.value()).isEqualTo(input);
    }

    @ParameterizedTest(name = "[{index}] id=[{0}] should throw IllegalArgumentException")
    @NullAndEmptySource
    @ValueSource(strings = {"not-a-uuid-string", "550e8400-e29b-41d4-a716-44665544000"}) // Too short/invalid format
    void should_reject_invalid_profile_id(String invalidInput) {
        assertThatThrownBy(() -> new ProfileId(invalidInput))
                .isInstanceOf(IllegalArgumentException.class); 
    }

    private static Stream<Arguments> validIds() {
        return Stream.of(
                Arguments.of(UUID.randomUUID().toString()),
                Arguments.of("550e8400-e29b-41d4-a716-446655440000"),
                Arguments.of("6ba7b810-9dad-11d1-80b4-00c04fd430c8")
        );
    }
}
