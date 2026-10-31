package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    @ParameterizedTest(name = "[{index}] email={0} should be valid")
    @MethodSource("validEmails")
    void should_create_valid_email(String input) {
        Email email = new Email(input);
        assertThat(email.value()).isEqualTo(input.toLowerCase());
    }

    @ParameterizedTest(name = "[{index}] email={0} should be invalid")
    @MethodSource("invalidEmails")
    void should_reject_invalid_email(String input) {
        assertThatThrownBy(() -> new Email(input))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("test.name+tag@domain.co.uk"),
                Arguments.of("a@b.cc")
        );
    }

    private static Stream<Arguments> invalidEmails() {
        // JUnit 5 ParameterizedTest does not support null in MethodSource arguments directly for String parameters.
        // We handle null separately or use a wrapper, but simplest is to split streams or use @NullAndEmptySource if available.
        // Since we are using MethodSource, we must ensure the stream contains non-null elements for String parameter,
        // OR change signature to accept Object and cast. However, standard practice with MethodSource for mixed types
        // often involves separate tests or handling null explicitly in the test method logic if possible.
        // But here, the constructor throws on null immediately. 
        // To fix "arguments array must not be null" which usually happens when MethodSource returns null or empty,
        // or when passing null into a String param without proper handling.
        // Actually, the error "arguments array must not be null" specifically points to the Arguments object itself being null
        // or the stream returning null elements? No, it means the `arguments` passed to the test method invocation was null.
        // This often happens if `invalidEmails()` returns a stream that yields null as an element, or the method itself returned null.
        // Let's ensure we don't pass null directly into the String argument of Arguments.of if JUnit complains about type mismatch,
        // BUT actually JUnit allows null in Arguments.of(String.class). The issue might be elsewhere.
        // Wait, looking at previous code: `Arguments.of(null)` is valid.
        // The error `PreconditionViolationException: arguments array must not be null` typically occurs if the @MethodSource method
        // returns null instead of a Stream, OR if one of the Arguments objects inside the stream has a null *array* for its values (rare).
        // A common cause is mixing types incorrectly or the source method signature mismatch.
        // Another possibility: In some JUnit versions, passing null to a primitive wrapper or specific type causes issues if not handled.
        // However, the most robust fix for mixed null/non-null string tests with MethodSource is to use separate sources or handle carefully.
        
        // Let's look closer at the error: "initializationError". This suggests the test class failed to load/initialize.
        // If `invalidEmails` throws during static initialization? No.
        // Is it possible `Stream.of(Arguments.of(""))` etc is fine? Yes.
        
        // Re-evaluating: Did I miss an import? Or did `Arguments.of(null)` fail in this environment?
        // Actually, `org.junit.jupiter.params.provider.Arguments#of` handles nulls.
        // BUT, there is a known issue where if you have multiple `@ParameterizedTest` methods and one fails initialization...
        
        // Let's try splitting invalid emails into non-null strings and handling null separately via @NullAndEmptySource + ValueSource
        // But we are using MethodSource. 
        // Alternative: Use `@ValueSource` for strings and `@NullAndEmptySource` combined? No, they don't combine easily on same method without custom provider.
        
        // The safest bet for "arguments array must not be null" when using MethodSource is that the Stream itself was empty or null,
        // OR one of the Arguments objects had a problem.
        
        // Let's rewrite to ensure strict typing and avoid potential JUnit quirks with null in String args by separating them.
        return Stream.concat(
            Stream.of(
                Arguments.of(""),
                Arguments.of("no-at-sign"),
                Arguments.of("@example.com"),
                Arguments.of("user@"),
                Arguments.of("   ")
            ),
            Stream.of(Arguments.of((String) null)) // Explicit cast helps clarity but might not fix underlying JUnit bug if any
        );
    }
}
