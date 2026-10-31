package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

/**
 * 建立個人資料的 Command 物件。
 */
public record CreateProfileCommand(
        String name,
        String email
) {
    public CreateProfileCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
    }
}
