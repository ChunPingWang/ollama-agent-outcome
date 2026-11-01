package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new profile.
 */
public record CreateProfileRequest(
    @NotBlank(message = "name must not be blank")
    @Size(max = 50, message = "name exceeds maximum length of 50")
    String name,

    @NotBlank(message = "email must not be blank")
    @Email(message = "invalid email format")
    String email
) {}
