package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Email address value object.
 */
public record Email(String address) {

    public Email {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
