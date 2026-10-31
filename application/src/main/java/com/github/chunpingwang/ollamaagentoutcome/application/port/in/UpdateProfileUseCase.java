package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Application layer inbound port for updating an existing profile.
 */
public interface UpdateProfileUseCase {
    void execute(UpdateProfileRequest request);
}
