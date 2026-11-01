package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * Outbound port for persisting or updating a profile entity.
 */
public interface SaveProfilePort {
    void save(Profile profile);
}
