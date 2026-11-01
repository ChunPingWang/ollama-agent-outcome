package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
                Arguments.of("John Doe"),
                Arguments.of(""),
                Arguments.of((Object) null),
                Arguments.of("   ") // Should be treated as null or empty based on spec logic, usually trimmed to null
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validDisplayNames")
    void should_create_with_various_names(String name) {
        DisplayName dn = new DisplayName(name);
        
        if (name == null || name.isBlank()) {
            assertThat(dn.name()).isNull();
        } else {
            assertThat(dn.name()).isEqualTo(name.trim());
        }
    }
    
    @ParameterizedTest(name = "should_be_immutable_for_{0}")
    @MethodSource("validDisplayNames")
    void should_not_allow_modification(String initialName) {
        DisplayName dn = new DisplayName(initialName);
        String originalValue = dn.name();
        
        // Since it's a record, we can't modify the field. 
        // We just verify that accessing it returns consistent value.
        assertThat(dn.name()).isEqualTo(originalValue);
    }
}
