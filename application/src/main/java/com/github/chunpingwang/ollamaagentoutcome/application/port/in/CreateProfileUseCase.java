package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {
    void execute(CreateProfileRequest request);
}
