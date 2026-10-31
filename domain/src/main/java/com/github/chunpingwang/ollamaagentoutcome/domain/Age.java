package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Age value object ensuring non-negative integer constraint.
 */
public record Age(int years) {
    public Age {
        if (years < 0) {
            throw new InvalidAgeException();
        }
    }
}
