package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.regex.Pattern;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

/**
 * 電子郵件地址值物件。
 */
public final class Email {

	private static final Pattern EMAIL_PATTERN = 
		Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	private final String address;

	public Email(String address) {
		if (address == null) {
			throw new InvalidInputException("email cannot be null");
		}
		String trimmed = address.strip();
		if (trimmed.isEmpty()) {
			throw new InvalidInputException("email cannot be empty");
		}
		if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
			throw new InvalidInputException("invalid email format");
		}
		this.address = trimmed.toLowerCase();
	}

	public String address() {
		return this.address;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Email other)) return false;
		return this.address.equals(other.address);
	}
	
	@Override
	public int hashCode() {
		return this.address.hashCode();
	}
}
