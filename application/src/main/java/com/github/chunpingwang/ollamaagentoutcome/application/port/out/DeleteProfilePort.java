package com.github.chunpingwang.ollamaagentoutcome.application.port.out;


import java.util.UUID;

/**
 * Outbound port for deleting a profile by ID.
 */
public interface DeleteProfilePort {
    void deleteById(UUID id);
}
