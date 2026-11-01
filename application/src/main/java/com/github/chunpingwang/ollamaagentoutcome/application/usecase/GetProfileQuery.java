package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * Query object for fetching a specific profile by its unique identifier.
 */
public record GetProfileQuery(UUID id) {
}
