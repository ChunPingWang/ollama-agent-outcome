package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import java.util.regex.Pattern;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.InvalidEmailException;

/**
 * Email Address Value Object，包含基本格式驗證與長度限制。
 */
public record Email(String address) {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 254;

    public Email {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailException(address);
        }
        String trimmed = address.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new InvalidEmailException(trimmed);
        }
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new InvalidEmailException(trimmed);
        }
    }

    /**
     * 建立新的 Email 實例（靜態工廠）。
     */
    public static Email of(String rawAddress) {
        return new Email(rawAddress != null ? rawAddress : "");
    }
}
