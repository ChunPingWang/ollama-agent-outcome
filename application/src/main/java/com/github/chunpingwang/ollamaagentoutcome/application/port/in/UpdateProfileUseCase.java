package com.github.chunpingwang.ollamaagentoutcome.application.port.in;


import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;

/**
 * Inbound port for updating an existing profile.
 */
public interface UpdateProfileUseCase {
    void execute(UpdateProfileCommand command);
}
