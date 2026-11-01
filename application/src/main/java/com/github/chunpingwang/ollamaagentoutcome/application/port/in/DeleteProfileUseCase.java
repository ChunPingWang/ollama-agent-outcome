package com.github.chunpingwang.ollamaagentoutcome.application.port.in;


import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;

/**
 * Inbound port for deleting a profile by ID.
 */
public interface DeleteProfileUseCase {
    void execute(DeleteProfileCommand command);
}
