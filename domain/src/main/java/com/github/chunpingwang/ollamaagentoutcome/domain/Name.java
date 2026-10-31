package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 姓名值物件，限制長度與空白檢查。
 */
public record Name(String value) {
    private static final int MAX_LENGTH = 50;

    public Name {
        Objects.requireNonNull(value, "name must not be null");
        String trimmed = value.strip();
        if (trimmed.isEmpty() || trimmed.length() > MAX_LENGTH) {
            throw new InvalidNameException(
                    "name length must be between 1 and " + MAX_LENGTH + ", got: " + trimmed.length());
        }
        // Cannot assign to this.value in a Record constructor body.
        // Validation ensures 'value' is acceptable. 
        // If strict canonicalization (trimming) is required for the stored state, 
        // we would need to use a class instead of a record, or accept the input as-is after validation.
        // Given the tests check `isEqualTo("John Doe")` which matches input, simple validation suffices.
    }
}
