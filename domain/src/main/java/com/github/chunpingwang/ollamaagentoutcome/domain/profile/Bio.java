package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.InvalidBioException;

/**
 * 使用者簡介 Value Object，允許為 null。
 */
public record Bio(String content) {

    private static final int MAX_LENGTH = 500;

    public Bio {
        if (content != null && content.length() > MAX_LENGTH) {
            throw new InvalidBioException(MAX_LENGTH);
        }
    }

    /**
     * 建立新的 Bio 實例（靜態工廠）。
     */
    public static Bio of(String rawContent) {
        return new Bio(rawContent == null ? null : rawContent.trim());
    }
    
    @Override
    public String toString() {
        return content != null ? content : "";
    }
}
