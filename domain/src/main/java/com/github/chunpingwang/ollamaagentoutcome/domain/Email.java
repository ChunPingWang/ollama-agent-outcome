package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Email address value object with basic RFC 5322 validation.
 */
public final class Email {
    // Basic regex for email validation as per spec requirement
    private static final String EMAIL_REGEX = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailFormatException("Email cannot be blank");
        }
        if (!address.matches(EMAIL_REGEX)) {
            throw new InvalidEmailFormatException("Invalid email format: " + address);
        }
        this.address = address.toLowerCase();
    }
}
