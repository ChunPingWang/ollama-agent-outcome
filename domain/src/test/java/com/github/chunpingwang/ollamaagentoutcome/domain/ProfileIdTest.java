package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProfileIdTest {

    @ParameterizedTest(name = "[{index}] id={0}")
    @MethodSource("validUuids")
    void should_create_valid_profile_id(String uuidString) {
        ProfileId profileId = new ProfileId(uuidString);
        assertThat(profileId.value()).isEqualTo(UUID.fromString(uuidString).toString());
    }

    private static Stream<Arguments> validUuids() {
        return Stream.of(
            Arguments.of("550e8400-e29b-41d4-a716-446655440000"),
            Arguments.of(UUID.randomUUID().toString())
        );
    }

    @Test
    void should_reject_invalid_uuid_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("格式無效");
    }
}
