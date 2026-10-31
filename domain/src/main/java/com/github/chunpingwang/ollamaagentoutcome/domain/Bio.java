package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人簡介 Value Object，允許為空或空白字串。
 */
public record Bio(String value) {
    public Bio {
        // Bio can be null or empty, no validation needed other than immutability via record
    }

    /**
     * Convenience constructor for creating an empty bio.
     */
    public Bio() {
        this("");
    }
}
