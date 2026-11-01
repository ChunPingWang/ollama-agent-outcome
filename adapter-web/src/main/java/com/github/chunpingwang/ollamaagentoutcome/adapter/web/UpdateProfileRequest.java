package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for updating an existing profile.
 */
public record UpdateProfileRequest(
    @NotBlank(message = "email must not be blank")
    @Email(message = "invalid email format")
    String email,

    @NotBlank(message = "display name must not be blank")
    String displayName
) {
}
