package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Application layer inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {
    void execute(CreateProfileRequest request);
}
