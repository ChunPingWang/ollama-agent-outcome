package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * Email Address Value Object with validation. Immutable.
 */
public final class Email {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Invalid email: must not be blank");
        }
        this.address = address.toLowerCase();
        if (!this.address.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format: " + address);
        }
    }

    /**
     * Returns the normalized email address string.
     */
    public String value() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return java.util.Objects.equals(address, other.address);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(address);
    }
}
