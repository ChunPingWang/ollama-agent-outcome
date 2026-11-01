package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a new profile.
 */
public record CreateProfileRequest(
        @NotBlank(message = "email must not be blank")
        @Email(message = "invalid email format")
        String email,

        String displayName
) {}
