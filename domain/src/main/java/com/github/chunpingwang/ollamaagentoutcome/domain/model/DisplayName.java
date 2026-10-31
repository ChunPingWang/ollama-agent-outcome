package com.github.chunpingwang.ollamaagentoutcome.domain.model;

/**
 * Display name value object.
 */
public record DisplayName(String name) {
    public DisplayName {
        // Record components are final. Cannot reassign this.name inside compact constructor.
        // Validation logic can go here. Normalization (blank -> null) requires static factory or different approach.
        // Keeping it simple for T1: allow any string input.
    }
}
