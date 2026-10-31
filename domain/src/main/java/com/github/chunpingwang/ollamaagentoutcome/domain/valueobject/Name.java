package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

/**
 * 姓名值物件。
 */
public final class Name {

	private static final int MAX_LENGTH = 255;

	private final String text;

	public Name(String text) {
		if (text == null) {
			throw new InvalidInputException("name cannot be null");
		}
		String trimmed = text.strip();
		if (trimmed.isEmpty()) {
			throw new InvalidInputException("name cannot be empty");
		}
		if (trimmed.length() > MAX_LENGTH) {
			throw new InvalidInputException("name length must be between 1 and " + MAX_LENGTH);
		}
		this.text = trimmed;
	}

	public String text() {
		return this.text;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Name other)) return false;
		return this.text.equals(other.text);
	}
	
	@Override
	public int hashCode() {
		return this.text.hashCode();
	}
}
