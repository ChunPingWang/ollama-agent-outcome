package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Inbound port for deleting a profile by ID.
 */
public interface DeleteProfileUseCase {
    void execute(String id);
}
