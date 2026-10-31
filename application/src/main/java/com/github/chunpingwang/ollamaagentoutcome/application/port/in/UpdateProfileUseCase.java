package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Inbound port for updating an existing profile.
 */
public interface UpdateProfileUseCase {
    void execute(UpdateProfileRequest request, String id);
}
