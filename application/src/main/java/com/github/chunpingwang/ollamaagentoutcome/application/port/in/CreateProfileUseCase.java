package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileCommand;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {
    void execute(CreateProfileCommand command);
}
